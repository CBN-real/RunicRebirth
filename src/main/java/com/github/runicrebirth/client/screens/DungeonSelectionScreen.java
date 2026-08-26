package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientDungeonData;
import com.github.runicrebirth.network.SelectDungeonC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class DungeonSelectionScreen extends Screen {

    private static final int NODE_SIZE = 40;
    private static final int NODE_SPACING = 80;
    private static final int BG_COLOR = 0xCC1A1A2E;
    private static final int NODE_COLOR = 0xFF3A3A5E;
    private static final int NODE_SELECTED = 0xFF6A4ADE;
    private static final int LINE_COLOR = 0xFF5A5A8A;

    private static final ResourceLocation[] TIER_IDS = {
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "acolyte"),
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "adept"),
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "arch"),
    };

    private final BlockPos controllerPos;
    private ResourceLocation selectedTierId;
    private int selectedDifficulty = 1;

    private int[][] nodePositions;

    public DungeonSelectionScreen(BlockPos controllerPos) {
        super(Component.literal("Dimensional Oculus"));
        this.controllerPos = controllerPos;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2 - 30;

        nodePositions = new int[][]{
                {centerX - NODE_SPACING, centerY},
                {centerX, centerY},
                {centerX + NODE_SPACING, centerY},
        };

        this.addRenderableWidget(Button.builder(Component.literal("Attune Portal"), btn -> attuneDungeon())
                .bounds(centerX - 50, this.height - 50, 100, 20)
                .build());

        for (int d = 1; d <= 3; d++) {
            final int diff = d;
            this.addRenderableWidget(Button.builder(Component.literal("D" + d), btn -> selectedDifficulty = diff)
                    .bounds(centerX - 70 + (d - 1) * 45, this.height - 75, 40, 20)
                    .build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, BG_COLOR);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFD4AF37);

        for (int i = 0; i < TIER_IDS.length - 1; i++) {
            drawLine(graphics, nodePositions[i][0], nodePositions[i][1],
                    nodePositions[i + 1][0], nodePositions[i + 1][1], LINE_COLOR);
        }

        for (int i = 0; i < TIER_IDS.length; i++) {
            ResourceLocation tierId = TIER_IDS[i];
            int nx = nodePositions[i][0];
            int ny = nodePositions[i][1];
            boolean isSelected = tierId.equals(selectedTierId);
            int color = isSelected ? NODE_SELECTED : NODE_COLOR;

            graphics.fill(nx - NODE_SIZE / 2, ny - NODE_SIZE / 2,
                    nx + NODE_SIZE / 2, ny + NODE_SIZE / 2, color);
            int borderColor = isSelected ? 0xFFD4AF37 : 0xFF8A8AAA;
            graphics.renderOutline(nx - NODE_SIZE / 2, ny - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE, borderColor);

            String label = tierId.getPath();
            graphics.drawCenteredString(this.font, label, nx, ny - 4, 0xFFFFFFFF);
        }

        if (selectedTierId != null) {
            int infoY = this.height - 110;
            graphics.drawCenteredString(this.font, selectedTierId.getPath(), this.width / 2, infoY, 0xFFD4AF37);
            int maxDiff = ClientDungeonData.getMaxSelectableDifficulty(selectedTierId, 3);
            graphics.drawCenteredString(this.font,
                    "Difficulty: " + selectedDifficulty + " / 3"
                            + "  (Max unlocked: " + maxDiff + ")",
                    this.width / 2, infoY + 12, 0xFF8888CC);
        }

        graphics.drawString(this.font, "KP: " + ClientDungeonData.getKnowledgePoints(), 10, 10, 0xFFD4AF37);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < TIER_IDS.length; i++) {
                int nx = nodePositions[i][0];
                int ny = nodePositions[i][1];
                if (mouseX >= nx - NODE_SIZE / 2 && mouseX <= nx + NODE_SIZE / 2
                        && mouseY >= ny - NODE_SIZE / 2 && mouseY <= ny + NODE_SIZE / 2) {
                    selectedTierId = TIER_IDS[i];
                    selectedDifficulty = 1;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void attuneDungeon() {
        if (selectedTierId == null) return;

        int maxDiff = ClientDungeonData.getMaxSelectableDifficulty(selectedTierId, 3);
        if (selectedDifficulty > maxDiff) {
            selectedDifficulty = maxDiff;
        }

        PacketDistributor.sendToServer(new SelectDungeonC2SPacket(
                selectedTierId, selectedDifficulty, controllerPos));
        this.onClose();
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int midY = (y1 + y2) / 2;
        graphics.fill(Math.min(x1, x2), midY, Math.max(x1, x2) + 1, midY + 1, color);
        graphics.fill(x1, Math.min(y1, midY), x1 + 1, Math.max(y1, midY), color);
        graphics.fill(x2, Math.min(y2, midY), x2 + 1, Math.max(y2, midY), color);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
