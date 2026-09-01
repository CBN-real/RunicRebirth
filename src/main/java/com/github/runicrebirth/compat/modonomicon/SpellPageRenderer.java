package com.github.runicrebirth.compat.modonomicon;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.BookDisplayState;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class SpellPageRenderer extends BookPageRenderer<SpellPage> {

    private static final int TEXT_COLOR = 0xFF1A1A2E;
    private static final int LABEL_COLOR = 0xFFAF9ACB;
    private static final int TITLE_COLOR = 0xFF2D1B4E;
    private static final int ICON_SIZE = 32;
    private static final int ICON_SPACING = 2;
    private static final int ICON_RENDER_SIZE = 16;
    private static final int OUTLINE_COLOR = 0xFFD4AF37;
    private static final int OUTLINE_PADDING = 1;

    private static final String[] ELEMENT_IDS = {
        "runicrebirth:arcane",
        "runicrebirth:fire",
        "runicrebirth:earth",
        "runicrebirth:ice",
        "runicrebirth:wind",
    };

    private static final Identifier[] ELEMENT_ICONS = {
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/gui/sprites/arcane_icon.png"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/gui/sprites/fire_icon.png"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/gui/sprites/earth_icon.png"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/gui/sprites/ice_icon.png"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/gui/sprites/wind_icon.png"),
    };

    private int selectedIndex = 0;
    private int iconRowY;
    private int iconStartX;

    public SpellPageRenderer(SpellPage page) {
        super(page);
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);
        selectedIndex = 0;
        BookDisplayState.setSelectedElement(ELEMENT_IDS[0]);
        BookDisplayState.setOffsets(page.getOffsetX(), page.getOffsetY(), page.getOffsetZ());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float ticks) {
        int x = -5;
        int y = 12;
        int pageWidth = BookEntryScreen.PAGE_WIDTH;

        var title = Component.literal(page.getSpellName()).withStyle(Style.EMPTY.withBold(true));
        int titleWidth = this.font.width(title);
        guiGraphics.text(this.font, title, (pageWidth - titleWidth) / 2 - 5, y, TITLE_COLOR, false);
        y += 16;

        guiGraphics.fill(10, y, pageWidth - 10, y + 1, 0xFF8B7BAA);
        y += 8;

        if (!page.isHideStats()) {
            drawStatLine(guiGraphics, "Damage:", page.getDamage(), x + 10, y);
            y += 14;

            drawStatLine(guiGraphics, "Range:", page.getRange(), x + 10, y);
            y += 14;

            drawStatLine(guiGraphics, "Type:", page.getDamageType(), x + 10, y);
            y += 20;
        }

        guiGraphics.text(this.font, Component.literal("Elements:").withStyle(Style.EMPTY.withBold(true)),
            x + 10, y, LABEL_COLOR, false);
        y += 14;

        int totalIconWidth = ELEMENT_ICONS.length * ICON_RENDER_SIZE + (ELEMENT_ICONS.length - 1) * ICON_SPACING;
        iconStartX = (pageWidth - totalIconWidth) / 2;
        iconRowY = y;

        for (int i = 0; i < ELEMENT_ICONS.length; i++) {
            int ix = iconStartX + i * (ICON_RENDER_SIZE + ICON_SPACING);

            if (i == selectedIndex) {
                int ox = ix - OUTLINE_PADDING;
                int oy = iconRowY - OUTLINE_PADDING;
                int ow = ICON_RENDER_SIZE + OUTLINE_PADDING * 2;
                int oh = ICON_RENDER_SIZE + OUTLINE_PADDING * 2;
                guiGraphics.fill(ox, oy, ox + ow, oy + 1, OUTLINE_COLOR);
                guiGraphics.fill(ox, oy + oh - 1, ox + ow, oy + oh, OUTLINE_COLOR);
                guiGraphics.fill(ox, oy, ox + 1, oy + oh, OUTLINE_COLOR);
                guiGraphics.fill(ox + ow - 1, oy, ox + ow, oy + oh, OUTLINE_COLOR);
            }

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ELEMENT_ICONS[i], ix, iconRowY, ICON_RENDER_SIZE, ICON_RENDER_SIZE);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean consumed) {
        if (event.button() != 0) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        for (int i = 0; i < ELEMENT_ICONS.length; i++) {
            int ix = iconStartX + i * (ICON_RENDER_SIZE + ICON_SPACING);
            if (mouseX >= ix && mouseX < ix + ICON_RENDER_SIZE
                && mouseY >= iconRowY && mouseY < iconRowY + ICON_RENDER_SIZE) {
                selectedIndex = i;
                BookDisplayState.setSelectedElement(ELEMENT_IDS[i]);
                return true;
            }
        }
        return false;
    }

    private void drawStatLine(GuiGraphicsExtractor guiGraphics, String label, String value, int x, int y) {
        var labelComponent = Component.literal(label + " ").withStyle(Style.EMPTY.withBold(true));
        var valueComponent = Component.literal(value);
        int labelWidth = this.font.width(labelComponent);
        guiGraphics.text(this.font, labelComponent, x, y, LABEL_COLOR, false);
        guiGraphics.text(this.font, valueComponent, x + labelWidth, y, TEXT_COLOR, false);
    }

    @Override
    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY) {
        return null;
    }
}
