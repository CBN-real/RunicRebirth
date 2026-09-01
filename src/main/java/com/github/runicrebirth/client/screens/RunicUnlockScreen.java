package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.client.ClientDungeonData;
import com.github.runicrebirth.network.AttemptUnlockC2SPacket;
import com.github.runicrebirth.unlock.UnlockCategory;
import com.github.runicrebirth.unlock.UnlockEntry;
import com.github.runicrebirth.unlock.UnlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RunicUnlockScreen extends Screen {

    private final BlockPos cushionPos;
    private UnlockCategory activeTab = UnlockCategory.SPELLS;

    private static final int CELL = 28;
    private static final int ICON_SIZE = 20;
    private static final int BG_WIDTH = 350;
    private static final int BG_HEIGHT = 240;
    private static final int TAB_W = 60;
    private static final int TAB_H = 18;

    public RunicUnlockScreen(BlockPos cushionPos) {
        super(Component.translatable("screen.runicrebirth.unlock_tree"));
        this.cushionPos = cushionPos;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor gfx) {
        // suppress blur
    }

  @Override
    protected void init() {
        super.init();
        int left = (width - BG_WIDTH) / 2;
        int top = (height - BG_HEIGHT) / 2;
        UnlockCategory[] cats = UnlockCategory.values();
        for (int i = 0; i < cats.length; i++) {
            final UnlockCategory cat = cats[i];
            int tabX = left + i * (TAB_W + 2);
            int tabY = top - TAB_H;
            addRenderableWidget(Button.builder(Component.literal(cat.name()), btn -> {
                activeTab = cat;
            }).pos(tabX, tabY).size(TAB_W, TAB_H).build());
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gg, int mx, int my, float partial) {
        int left = (width - BG_WIDTH) / 2;
        int top = (height - BG_HEIGHT) / 2;

        gg.fill(left, top, left + BG_WIDTH, top + BG_HEIGHT, 0xCC1A1A2E);
        gg.outline(left, top, left + BG_WIDTH, top + BG_HEIGHT, 0xFF4A4A8A);

        gg.centeredText(font, title, width / 2, top + 5, 0xFFE0D0FF);

        int kp = ClientDungeonData.getKnowledgePoints();
        gg.text(font, "KP: " + kp, left + BG_WIDTH - 60, top + 5, 0xFFD4AF37);

        List<UnlockEntry> entries = UnlockRegistry.forCategory(activeTab);
        Map<Identifier, int[]> positions = computePositions(entries, left, top + TAB_H + 5);

        for (UnlockEntry entry : entries) {
            if (entry.getParentId() != null) {
                int[] parentPos = positions.get(entry.getParentId());
                int[] childPos = positions.get(entry.getId());
                if (parentPos != null && childPos != null) {
                    drawConnector(gg, parentPos[0], parentPos[1], childPos[0], childPos[1]);
                }
            }
        }

        for (UnlockEntry entry : entries) {
            int[] pos = positions.get(entry.getId());
            if (pos == null) continue;
            boolean unlocked = ClientDungeonData.isUnlocked(entry.getId());
            int bgColor = unlocked ? 0xFF2A6A2A : 0xFF2A2A4A;
            int cx = pos[0];
            int cy = pos[1];
            int r = ICON_SIZE / 2;
            gg.fill(cx - r, cy - r, cx + r, cy + r, bgColor);
            gg.outline(cx - r, cy - r, cx + r, cy + r, unlocked ? 0xFF60C060 : 0xFF6060C0);
        }

        List<Component> hoverTooltip = null;
        for (UnlockEntry entry : entries) {
            int[] pos = positions.get(entry.getId());
            if (pos == null) continue;
            int r = ICON_SIZE / 2;
            if (mx >= pos[0] - r && mx <= pos[0] + r && my >= pos[1] - r && my <= pos[1] + r) {
                boolean unlocked = ClientDungeonData.isUnlocked(entry.getId());
                hoverTooltip = new ArrayList<>();
                hoverTooltip.add(Component.literal(entry.getDisplayName()).withStyle(ChatFormatting.GOLD));
                hoverTooltip.add(Component.literal("KP Cost: " + entry.getKpCost()).withStyle(ChatFormatting.AQUA));
                hoverTooltip.add(Component.literal(unlocked ? "UNLOCKED" : "LOCKED")
                        .withStyle(unlocked ? ChatFormatting.GREEN : ChatFormatting.RED));
                for (net.minecraft.resources.Identifier cond : entry.getAdvancementConditions()) {
                    hoverTooltip.add(Component.literal("Requires: " + cond.getPath())
                            .withStyle(ChatFormatting.GRAY));
                }
                break;
            }
        }

        super.extractRenderState(gg, mx, my, partial);

        if (hoverTooltip != null) {
            gg.setTooltipForNextFrame(font, hoverTooltip, Optional.empty(), mx, my);
        }
    }

    private void drawConnector(GuiGraphicsExtractor gg, int x1, int y1, int x2, int y2) {
        int midY = (y1 + y2) / 2;
        gg.fill(x1, Math.min(y1, midY), x1 + 1, Math.max(y1, midY), 0xFF8080C0);
        gg.fill(Math.min(x1, x2), midY, Math.max(x1, x2) + 1, midY + 1, 0xFF8080C0);
        gg.fill(x2, Math.min(midY, y2), x2 + 1, Math.max(midY, y2), 0xFF8080C0);
    }

    private Map<Identifier, int[]> computePositions(List<UnlockEntry> entries, int baseX, int baseY) {
        Map<Identifier, int[]> positions = new HashMap<>();
        for (UnlockEntry entry : entries) {
            int px = baseX + 20 + entry.getGridCol() * CELL;
            int py = baseY + 20 + entry.getGridRow() * CELL;
            positions.put(entry.getId(), new int[]{px, py});
        }
        return positions;
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean handleInteractions) {
        double mx = event.x();
        double my = event.y();
        if (event.button() == 0) {
            List<UnlockEntry> entries = UnlockRegistry.forCategory(activeTab);
            int left = (width - BG_WIDTH) / 2;
            int top = (height - BG_HEIGHT) / 2;
            Map<Identifier, int[]> positions = computePositions(entries, left, top + TAB_H + 5);
            int r = ICON_SIZE / 2;
            for (UnlockEntry entry : entries) {
                int[] pos = positions.get(entry.getId());
                if (pos == null) continue;
                if (mx >= pos[0] - r && mx <= pos[0] + r && my >= pos[1] - r && my <= pos[1] + r) {
                    if (!ClientDungeonData.isUnlocked(entry.getId())) {
                        ClientPacketDistributor.sendToServer(new AttemptUnlockC2SPacket(entry.getId(), cushionPos));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(event, handleInteractions);
    }
}
