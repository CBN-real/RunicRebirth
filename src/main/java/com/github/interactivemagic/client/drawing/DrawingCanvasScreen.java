package com.github.interactivemagic.client.drawing;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.client.input.ModKeyMappings;
import com.github.interactivemagic.config.ClientConfig;
import com.github.interactivemagic.magic.recognition.StrokePoint;
import com.github.interactivemagic.network.CancelDrawC2SPacket;
import com.github.interactivemagic.network.DrawSubmitC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class DrawingCanvasScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/gui/canvas_gui.png");
    private static final int TEX_W = 512;
    private static final int TEX_H = 512;

    private static final int ELEMENT_COL_W = 24;
    private static final int ELEMENT_ICON = 16;
    private static final int ELEMENT_PAD = (ELEMENT_COL_W - ELEMENT_ICON) / 2;
    private static final int ELEMENT_GAP = 4;
    private static final int ELEM_CANVAS_GAP = 8;
    private static final int CANVAS_REF_GAP = 8;
    private static final int REF_BOX = 22;
    private static final int REF_BOX_GAP = 2;
    private static final int REF_COLS = 4;
    private static final int SECTION_HEADER_H = 11;
    private static final int SECTION_GAP = 4;

    private static final int BG_COLOR = 0xC0202030;
    private static final int FRAME_COLOR = 0xFFE0C870;
    private static final int CANVAS_BORDER = 0xFF605040;
    private static final int BOX_BG = 0xA0181820;
    private static final int BOX_SPELL_BORDER = 0xFFE0C870;
    private static final int BOX_MOD_BORDER = 0xFF70A0E0;
    private static final int BOX_SEL_BORDER = 0xFFFFFFFF;
    private static final int ELEM_SEL_BORDER = 0xFFFFFFFF;
    private static final int SPELL_HEADER_COL = 0xFFE0C870;
    private static final int MOD_HEADER_COL = 0xFF70A0E0;

    private record ShapeRef(String shapeId, String iconName, boolean isModifier) {
        ResourceLocation iconPath() {
            String suffix = isModifier ? "_icon_small" : "_icon";
            return ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID,
                "textures/gui/shape_icons/" + iconName + suffix + ".png");
        }
        int iconSize() { return isModifier ? 11 : 19; }
    }

    private record RefSection(String titleKey, boolean modSection, List<ShapeRef> shapes) {}

    private static final List<RefSection> SECTIONS = List.of(
        new RefSection("gui.interactivemagic.beg_spells", false, List.of(
            new ShapeRef("line_down", "line", false),
            new ShapeRef("circle", "circle", false),
            new ShapeRef("v_shape", "v", false),
            new ShapeRef("arrow", "arrow", false)
        )),
        new RefSection("gui.interactivemagic.int_spells", false, List.of(
            new ShapeRef("shield", "shield", false),
            new ShapeRef("slash", "slash", false),
            new ShapeRef("explosion", "explosion", false),
            new ShapeRef("meteor", "meteor", false)
        )),
        new RefSection("gui.interactivemagic.adv_spells", false, List.of(
            new ShapeRef("binding", "binding", false),
            new ShapeRef("hammer", "hammer", false),
            new ShapeRef("ballista", "ballista", false)
        )),
        new RefSection("gui.interactivemagic.beg_mods", true, List.of(
            new ShapeRef("plus", "plus", true),
            new ShapeRef("two_casts", "two_casts", true),
            new ShapeRef("range", "range", true)
        )),
        new RefSection("gui.interactivemagic.int_mods", true, List.of(
            new ShapeRef("cooldown", "cooldown", true),
            new ShapeRef("plus_two", "plus_two", true),
            new ShapeRef("charges", "charges", true)
        )),
        new RefSection("gui.interactivemagic.adv_mods", true, List.of(
            new ShapeRef("plus_four", "plus_four", true),
            new ShapeRef("four_casts", "four_casts", true)
        ))
    );

    private static final float TEXTURE_ROTATION_SPEED = 0.2f;

    private final StrokeBuffer buffer = new StrokeBuffer();
    private boolean drawing;
    private boolean sentTerminal;
    private float textureAngle;

    private int guiLeft, guiTop, totalW, totalH;
    private int canvasX, canvasY, canvasSize;
    private int canvasCenterX, canvasCenterY;
    private int drawRadius;
    private int elementBarX, elementBarY;
    private int refPanelX, refPanelY, refPanelW, refPanelH;

    private final List<Element> elements = new ArrayList<>();
    private int selectedElementIdx;
    private ShapeRef selectedRef;

    public DrawingCanvasScreen() {
        super(Component.translatable("screen.interactivemagic.drawing_canvas"));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        RenderSystem.disableDepthTest();
        this.minecraft.getMainRenderTarget().bindWrite(false);
    }

    @Override
    protected void init() {
        elements.clear();
        ElementRegistry.REGISTRY.forEach(elements::add);
        if (selectedElementIdx >= elements.size()) selectedElementIdx = 0;

        refPanelW = REF_COLS * REF_BOX + (REF_COLS - 1) * REF_BOX_GAP;

        canvasSize = Math.min((int) (height * 0.9), height - 22);
        int maxCanvasW = width - ELEMENT_COL_W - ELEM_CANVAS_GAP - CANVAS_REF_GAP - refPanelW - 4;
        canvasSize = Math.max(50, Math.min(canvasSize, maxCanvasW));

        totalW = ELEMENT_COL_W + ELEM_CANVAS_GAP + canvasSize + CANVAS_REF_GAP + refPanelW;
        totalH = canvasSize;

        int idealCX = width / 2;
        int idealCY = height / 2;

        canvasX = idealCX - canvasSize / 2;
        int leftNeeded = ELEMENT_COL_W + ELEM_CANVAS_GAP;
        int rightNeeded = CANVAS_REF_GAP + refPanelW;
        if (canvasX < leftNeeded) canvasX = leftNeeded;
        if (canvasX + canvasSize + rightNeeded > width) canvasX = width - canvasSize - rightNeeded;

        canvasY = idealCY - canvasSize / 2;
        canvasY = Math.max(0, Math.min(canvasY, height - 22 - canvasSize));

        canvasCenterX = canvasX + canvasSize / 2;
        canvasCenterY = canvasY + canvasSize / 2;
        drawRadius = (int) (canvasSize * 0.67) / 2;

        elementBarX = canvasX - ELEM_CANVAS_GAP - ELEMENT_COL_W;
        elementBarY = canvasY;

        guiLeft = elementBarX;
        guiTop = elementBarY;

        refPanelX = canvasX + canvasSize + CANVAS_REF_GAP;
        refPanelY = canvasY;
        refPanelH = canvasSize;
    }

    private Element selectedElement() {
        return elements.isEmpty() ? null : elements.get(selectedElementIdx);
    }

    private int strokeColor() {
        Element e = selectedElement();
        return e != null ? (0xFF000000 | e.displayColor()) : 0xFFAE78FF;
    }

    // ---- Rendering ----

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        textureAngle = (textureAngle + partialTick * TEXTURE_ROTATION_SPEED) % 360f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        renderPanelBackgrounds(g);
        renderElementBar(g);
        renderCanvasFrame(g);
        if (selectedRef != null) renderReferenceOverlay(g);
        renderStrokes(g);
        renderRefPanel(g);

        int hintY = guiTop - 12;
        if (hintY >= 0) {
            g.drawCenteredString(font, Component.translatable("screen.interactivemagic.canvas_hint"),
                width / 2, hintY, 0xAAFFFFFF);
        }

        RenderSystem.disableBlend();
    }

    private void renderPanelBackgrounds(GuiGraphics g) {
        g.fill(elementBarX, elementBarY, elementBarX + ELEMENT_COL_W, elementBarY + canvasSize, BG_COLOR);
        drawRect(g, elementBarX - 1, elementBarY - 1, ELEMENT_COL_W + 2, canvasSize + 2, FRAME_COLOR);

        g.fill(refPanelX, refPanelY, refPanelX + refPanelW, refPanelY + refPanelH, BG_COLOR);
        drawRect(g, refPanelX - 1, refPanelY - 1, refPanelW + 2, refPanelH + 2, FRAME_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int scaled_diff = (int) (canvasSize * 1.05) - canvasSize;
        g.pose().pushPose();
        g.pose().translate(canvasCenterX, canvasCenterY, 0);
        g.pose().mulPose(Axis.ZP.rotationDegrees(textureAngle));
        g.pose().translate(-canvasCenterX, -canvasCenterY, 0);
        g.blit(GUI_TEXTURE, canvasX - scaled_diff / 2, canvasY - scaled_diff / 2, canvasSize + scaled_diff, canvasSize + scaled_diff,
            0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
        g.pose().popPose();
        RenderSystem.disableBlend();
    }

    private void renderElementBar(GuiGraphics g) {
        if (elements.isEmpty()) return;
        int iconsH = elements.size() * ELEMENT_ICON + (elements.size() - 1) * ELEMENT_GAP;
        int iconX = elementBarX + ELEMENT_PAD;
        int startY = elementBarY + (canvasSize - iconsH) / 2;

        for (int i = 0; i < elements.size(); i++) {
            Element el = elements.get(i);
            int iy = startY + i * (ELEMENT_ICON + ELEMENT_GAP);
            g.fill(iconX, iy, iconX + ELEMENT_ICON, iy + ELEMENT_ICON, 0xFF000000 | el.displayColor());
            if (i == selectedElementIdx) {
                drawRect(g, iconX - 1, iy - 1, ELEMENT_ICON + 2, ELEMENT_ICON + 2, ELEM_SEL_BORDER);
            }
        }
    }

    private void renderCanvasFrame(GuiGraphics g) {
        drawRect(g, canvasX - 1, canvasY - 1, canvasSize + 2, canvasSize + 2, CANVAS_BORDER);
        drawCircle(g, canvasCenterX, canvasCenterY, drawRadius, CANVAS_BORDER);
    }

    private void renderReferenceOverlay(GuiGraphics g) {
        int overlaySize = canvasSize * 40 / 100;
        int ox = canvasX + (canvasSize - overlaySize) / 2;
        int oy = canvasY + (canvasSize - overlaySize) / 2;
        int src = selectedRef.iconSize();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.blit(selectedRef.iconPath(), ox, oy, overlaySize, overlaySize,
            0, 0, src, src, src, src);
        RenderSystem.disableBlend();
    }

    private void renderStrokes(GuiGraphics g) {
        int color = strokeColor();
        for (List<StrokePoint> stroke : buffer.strokes()) {
            for (int i = 1; i < stroke.size(); i++) {
                StrokePoint a = stroke.get(i - 1);
                StrokePoint b = stroke.get(i);
                drawLine(g, canvasX + a.x(), canvasY + a.y(),
                    canvasX + b.x(), canvasY + b.y(), color);
            }
        }
    }

    private void renderRefPanel(GuiGraphics g) {
        int y = refPanelY + 4;
        for (RefSection section : SECTIONS) {
            int headerCol = section.modSection() ? MOD_HEADER_COL : SPELL_HEADER_COL;
            g.drawString(font, Component.translatable(section.titleKey()), refPanelX + 1, y, headerCol, false);
            y += SECTION_HEADER_H;

            for (int i = 0; i < section.shapes().size(); i++) {
                ShapeRef ref = section.shapes().get(i);
                int col = i % REF_COLS;
                int bx = refPanelX + col * (REF_BOX + REF_BOX_GAP);
                int by = y;

                g.fill(bx, by, bx + REF_BOX, by + REF_BOX, BOX_BG);

                int border = ref.isModifier() ? BOX_MOD_BORDER : BOX_SPELL_BORDER;
                if (selectedRef != null && selectedRef.shapeId().equals(ref.shapeId())) {
                    border = BOX_SEL_BORDER;
                }
                drawRect(g, bx, by, REF_BOX, REF_BOX, border);

                int iconSz = ref.iconSize();
                int iconX = bx + (REF_BOX - iconSz) / 2;
                int iconY = by + (REF_BOX - iconSz) / 2;
                g.blit(ref.iconPath(), iconX, iconY, 0, 0, iconSz, iconSz, iconSz, iconSz);
            }

            y += REF_BOX + SECTION_GAP;
        }
    }

    // ---- Input ----

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (tryClickElement(mx, my)) return true;
            if (tryClickRefBox(mx, my)) return true;
            if (insideDrawCircle(mx, my)) {
                drawing = true;
                buffer.beginStroke();
                buffer.appendPoint(mx - canvasX, my - canvasY, 0);
                return true;
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            submitAndClose();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (drawing && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            double cx = mx - canvasCenterX;
            double cy = my - canvasCenterY;
            double dist = Math.sqrt(cx * cx + cy * cy);
            if (dist > drawRadius) {
                mx = canvasCenterX + cx * drawRadius / dist;
                my = canvasCenterY + cy * drawRadius / dist;
            }
            buffer.appendPoint(mx - canvasX, my - canvasY, 3.0);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && drawing) {
            buffer.endStroke();
            drawing = false;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { cancelAndClose(); return true; }
        if (ModKeyMappings.isMovementKey(keyCode, scanCode)) { cancelAndClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ---- Hit testing ----

    private boolean insideDrawCircle(double mx, double my) {
        double dx = mx - canvasCenterX;
        double dy = my - canvasCenterY;
        return dx * dx + dy * dy <= (double) drawRadius * drawRadius;
    }

    private boolean tryClickElement(double mx, double my) {
        if (elements.isEmpty()) return false;
        int iconsH = elements.size() * ELEMENT_ICON + (elements.size() - 1) * ELEMENT_GAP;
        int iconX = elementBarX + ELEMENT_PAD;
        int startY = elementBarY + (canvasSize - iconsH) / 2;

        for (int i = 0; i < elements.size(); i++) {
            int iy = startY + i * (ELEMENT_ICON + ELEMENT_GAP);
            if (mx >= iconX && mx < iconX + ELEMENT_ICON && my >= iy && my < iy + ELEMENT_ICON) {
                selectedElementIdx = i;
                return true;
            }
        }
        return false;
    }

    private boolean tryClickRefBox(double mx, double my) {
        int y = refPanelY + 4;
        for (RefSection section : SECTIONS) {
            y += SECTION_HEADER_H;
            for (int i = 0; i < section.shapes().size(); i++) {
                int col = i % REF_COLS;
                int bx = refPanelX + col * (REF_BOX + REF_BOX_GAP);
                int by = y;
                if (mx >= bx && mx < bx + REF_BOX && my >= by && my < by + REF_BOX) {
                    ShapeRef clicked = section.shapes().get(i);
                    selectedRef = (selectedRef != null && selectedRef.shapeId().equals(clicked.shapeId()))
                        ? null : clicked;
                    return true;
                }
            }
            y += REF_BOX + SECTION_GAP;
        }
        return false;
    }

    // ---- Submit / Cancel ----

    private void submitAndClose() {
        if (sentTerminal) return;
        sentTerminal = true;
        if (buffer.isEmpty() || buffer.totalPoints() < 4) {
            PacketDistributor.sendToServer(new CancelDrawC2SPacket());
        } else {
            Element el = selectedElement();
            ResourceLocation elemId = el != null ? el.id()
                : ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "arcane");
            PacketDistributor.sendToServer(new DrawSubmitC2SPacket(buffer.strokes(), elemId));
        }
        if (Minecraft.getInstance().player != null) this.onClose();
    }

    private void cancelAndClose() {
        if (sentTerminal) return;
        sentTerminal = true;
        PacketDistributor.sendToServer(new CancelDrawC2SPacket());
        this.onClose();
    }

    @Override
    public void onClose() {
        if (!sentTerminal) {
            PacketDistributor.sendToServer(new CancelDrawC2SPacket());
            sentTerminal = true;
        }
        super.onClose();
    }

    // ---- Drawing helpers ----

    private static void drawRect(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y + 1, x + 1, y + h - 1, color);
        g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private static void drawCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        int segments = Math.max(64, radius * 2);
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            int px = cx + (int) Math.round(Math.cos(angle) * radius);
            int py = cy + (int) Math.round(Math.sin(angle) * radius);
            g.fill(px, py, px + 1, py + 1, color);
        }
    }

    private static void drawLine(GuiGraphics g, double x1, double y1, double x2, double y2, int color) {
        double dx = x2 - x1, dy = y2 - y1;
        double steps = Math.max(1, Math.ceil(Math.max(Math.abs(dx), Math.abs(dy))));
        for (int i = 0; i <= (int) steps; i++) {
            double t = i / steps;
            int xi = (int) Math.round(x1 + dx * t);
            int yi = (int) Math.round(y1 + dy * t);
            g.fill(xi, yi, xi + 2, yi + 2, color);
        }
    }
}
