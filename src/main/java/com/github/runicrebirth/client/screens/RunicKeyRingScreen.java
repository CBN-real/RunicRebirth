package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.menu.RunicKeyRingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RunicKeyRingScreen extends AbstractContainerScreen<RunicKeyRingMenu> {

    private static final int PANEL_COLOR    = 0xFFC6C6C6;
    private static final int SLOT_COLOR     = 0xFF8B8B8B;
    private static final int BORDER_DARK    = 0xFF555555;
    private static final int BORDER_LIGHT   = 0xFFFFFFFF;
    private static final int DIVIDER_COLOR  = 0xFF888888;
    private static final int TEXT_COLOR     = 0xFF404040;

    public RunicKeyRingScreen(RunicKeyRingMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth        = 176;
        this.imageHeight       = 184;
        this.inventoryLabelY   = 96;
        this.inventoryLabelX   = 8;
        this.titleLabelX       = 8;
        this.titleLabelY       = 6;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Panel
        g.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
        // Outer border
        g.fill(x, y, x + imageWidth, y + 1, BORDER_DARK);
        g.fill(x, y, x + 1, y + imageHeight, BORDER_DARK);
        g.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, BORDER_LIGHT);
        g.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, BORDER_LIGHT);

        // Dividers between sections
        g.fill(x + 7, y + 46, x + imageWidth - 7, y + 47, DIVIDER_COLOR);
        g.fill(x + 7, y + 96, x + imageWidth - 7, y + 97, DIVIDER_COLOR);
        g.fill(x + 7, y + 158, x + imageWidth - 7, y + 159, DIVIDER_COLOR);

        // Slot backgrounds
        for (Slot slot : this.menu.slots) {
            drawSlotBg(g, x + slot.x, y + slot.y);
        }
    }

    private void drawSlotBg(GuiGraphics g, int sx, int sy) {
        // Recessed slot: dark border top-left, light border bottom-right, gray interior
        g.fill(sx - 1, sy - 1, sx + 17, sy,      BORDER_DARK);
        g.fill(sx - 1, sy - 1, sx,      sy + 17,  BORDER_DARK);
        g.fill(sx,     sy + 16, sx + 17, sy + 17, BORDER_LIGHT);
        g.fill(sx + 16, sy,     sx + 17, sy + 16, BORDER_LIGHT);
        g.fill(sx, sy, sx + 16, sy + 16, SLOT_COLOR);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        // Title
        g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, TEXT_COLOR, false);
        // Section labels
        g.drawString(this.font,
            Component.translatable("container.runicrebirth.equipped_rings"),
            8, 17, TEXT_COLOR, false);
        g.drawString(this.font,
            Component.translatable("container.runicrebirth.key_ring_storage"),
            8, 49, TEXT_COLOR, false);
        // Player inventory label
        g.drawString(this.font, this.playerInventoryTitle,
            this.inventoryLabelX, this.inventoryLabelY, TEXT_COLOR, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }
}
