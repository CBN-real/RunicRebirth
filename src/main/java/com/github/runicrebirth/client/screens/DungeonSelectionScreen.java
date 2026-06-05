package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientDungeonData;
import com.github.runicrebirth.dungeon.DungeonType;
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
    private static final int NODE_LOCKED = 0xFF2A2A3E;
    private static final int LINE_COLOR = 0xFF5A5A8A;

    private final BlockPos controllerPos;
    private DungeonType selectedDungeon;
    private int selectedDifficulty = 1;

    // Node positions (screen-relative, set in init)
    private static final DungeonType[] NODE_ORDER = {
            DungeonType.ACOLYTE,
            DungeonType.FIRE_TRIAL,
            DungeonType.ICE_TRIAL,
            DungeonType.WIND_TRIAL,
            DungeonType.EARTH_TRIAL,
    };

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

        // Layout: Acolyte in center, 4 element trials around it
        nodePositions = new int[][]{
                {centerX, centerY},                           // Acolyte (center)
                {centerX - NODE_SPACING, centerY - NODE_SPACING},  // Fire (top-left)
                {centerX + NODE_SPACING, centerY - NODE_SPACING},  // Ice (top-right)
                {centerX - NODE_SPACING, centerY + NODE_SPACING},  // Wind (bottom-left)
                {centerX + NODE_SPACING, centerY + NODE_SPACING},  // Earth (bottom-right)
        };

        // Attune button
        this.addRenderableWidget(Button.builder(Component.literal("Attune Portal"), btn -> attuneDungeon())
                .bounds(centerX - 50, this.height - 50, 100, 20)
                .build());

        // Difficulty buttons
        for (int d = 1; d <= 3; d++) {
            final int diff = d;
            this.addRenderableWidget(Button.builder(Component.literal("D" + d), btn -> selectedDifficulty = diff)
                    .bounds(centerX - 70 + (d - 1) * 45, this.height - 75, 40, 20)
                    .build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Background
        graphics.fill(0, 0, this.width, this.height, BG_COLOR);

        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFD4AF37);

        // Connection lines from acolyte to each trial
        for (int i = 1; i < nodePositions.length; i++) {
            drawLine(graphics, nodePositions[0][0], nodePositions[0][1],
                    nodePositions[i][0], nodePositions[i][1], LINE_COLOR);
        }

        // Nodes
        for (int i = 0; i < NODE_ORDER.length; i++) {
            DungeonType type = NODE_ORDER[i];
            int nx = nodePositions[i][0];
            int ny = nodePositions[i][1];
            boolean isSelected = type == selectedDungeon;
            int color = isSelected ? NODE_SELECTED : NODE_COLOR;

            // Node box
            graphics.fill(nx - NODE_SIZE / 2, ny - NODE_SIZE / 2,
                    nx + NODE_SIZE / 2, ny + NODE_SIZE / 2, color);
            // Border
            int borderColor = isSelected ? 0xFFD4AF37 : 0xFF8A8AAA;
            graphics.renderOutline(nx - NODE_SIZE / 2, ny - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE, borderColor);

            // Label
            graphics.drawCenteredString(this.font, type.getDisplayName(),
                    nx, ny - 4, 0xFFFFFFFF);
        }

        // Selected dungeon info
        if (selectedDungeon != null) {
            int infoY = this.height - 110;
            graphics.drawCenteredString(this.font, selectedDungeon.getDisplayName(), this.width / 2, infoY, 0xFFD4AF37);
            graphics.drawCenteredString(this.font, selectedDungeon.getDescription(), this.width / 2, infoY + 12, 0xFFAAAAAA);

            int maxDiff = ClientDungeonData.getMaxSelectableDifficulty(
                    selectedDungeon.getId(), selectedDungeon.getMaxDifficulty());
            graphics.drawCenteredString(this.font,
                    "Difficulty: " + selectedDifficulty + " / " + selectedDungeon.getMaxDifficulty()
                            + "  (Max unlocked: " + maxDiff + ")",
                    this.width / 2, infoY + 24, 0xFF8888CC);
        }

        // Knowledge points display
        graphics.drawString(this.font, "KP: " + ClientDungeonData.getKnowledgePoints(), 10, 10, 0xFFD4AF37);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < NODE_ORDER.length; i++) {
                int nx = nodePositions[i][0];
                int ny = nodePositions[i][1];
                if (mouseX >= nx - NODE_SIZE / 2 && mouseX <= nx + NODE_SIZE / 2
                        && mouseY >= ny - NODE_SIZE / 2 && mouseY <= ny + NODE_SIZE / 2) {
                    selectedDungeon = NODE_ORDER[i];
                    selectedDifficulty = 1;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void attuneDungeon() {
        if (selectedDungeon == null) return;

        int maxDiff = ClientDungeonData.getMaxSelectableDifficulty(
                selectedDungeon.getId(), selectedDungeon.getMaxDifficulty());
        if (selectedDifficulty > maxDiff) {
            selectedDifficulty = maxDiff;
        }

        PacketDistributor.sendToServer(new SelectDungeonC2SPacket(
                selectedDungeon.getId(), selectedDifficulty, controllerPos));
        this.onClose();
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        // Simple line using fill — horizontal then vertical (L-shape)
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
