package com.github.runicrebirth.client.drawing;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.input.ModKeyMappings;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.github.runicrebirth.magic.recognition.StrokePoint;
import com.github.runicrebirth.network.CancelDrawC2SPacket;
import com.github.runicrebirth.network.CanvasSelectElementC2SPacket;
import com.github.runicrebirth.network.CanvasSelectTierC2SPacket;
import com.github.runicrebirth.network.DrawSubmitC2SPacket;
import com.github.runicrebirth.client.particles.InkParticle;
import com.github.runicrebirth.init.ModParticles;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class DrawingCanvasScreen extends Screen {

    private static final int CANVAS_BORDER = 0xFF605040;
    private static final int RADIAL_BG = 0xC0202030;
    private static final int RADIAL_HIGHLIGHT = 0xFFFFFFFF;
    private static final float RADIAL_ANIM_SPEED = 0.01f;
    private static final double INK_WORLD_RADIUS = 0.3;
    private static final double INK_PLANE_DISTANCE = 0.7;

    private static final float[] ELEMENT_X_FRAC = {0.27f, 0.21f, 0.19f, 0.225f, 0.30f};
    private static final float[] ELEMENT_Y_FRACS = {0.19f, 0.32f, 0.53f, 0.705f, 0.83f};
    private static final String[] ELEMENT_IDS = {"arcane", "fire", "ice", "earth", "wind"};

    private static final float DRAWING_X_FRAC = 0.5f;
    private static final float DRAWING_Y_FRAC = 0.5f;
    private static final float DRAWING_RADIUS_FRAC = 0.3f;

    private static final float[] TIER_X_FRAC = {0.82f, 0.84f, 0.82f};
    private static final float[] TIER_Y_FRACS = {0.18f, 0.46f, 0.785f};

    private static final float TIER_SELECTED_X = 0.80f;
    private static final float TIER_SELECTED_Y = 0.5f;

    private static final float ELEMENT_HIT_FRAC = 0.065f;
    private static final float TIER_HIT_FRAC = 0.085f;

    private static final Map<String, ResourceLocation> ELEMENT_ID_MAP = Map.of(
        "arcane", ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "arcane"),
        "fire", ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "fire"),
        "ice", ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "ice"),
        "earth", ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "earth"),
        "wind", ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "wind")
    );

    private record ShapeRef(String shapeId, String iconName, boolean isModifier) {
        ResourceLocation iconPath() {
            String suffix = isModifier ? "_icon_small_outline" : "_icon_outline";
            return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/gui/shape_icons/" + iconName + suffix + ".png");
        }
        int iconSize() { return isModifier ? 11 : 19; }
    }

    private record RefSection(String titleKey, List<ShapeRef> spells, List<ShapeRef> modifiers) {}

    private static final List<RefSection> SECTIONS = List.of(
        new RefSection("gui.runicrebirth.beg_spells", List.of(
            new ShapeRef("line_down", "line", false),
            new ShapeRef("circle", "circle", false),
            new ShapeRef("v_shape", "v", false),
            new ShapeRef("arrow", "arrow", false)
        ), List.of(
            new ShapeRef("plus", "plus", true),
            new ShapeRef("range", "range", true),
            new ShapeRef("two_casts", "two_casts", true)
        )),
        new RefSection("gui.runicrebirth.int_spells", List.of(
            new ShapeRef("shield", "shield", false),
            new ShapeRef("slash", "slash", false),
            new ShapeRef("explosion", "explosion", false),
            new ShapeRef("meteor", "meteor", false)
        ), List.of(
            new ShapeRef("plus_two", "plus_two", true),
            new ShapeRef("cooldown", "cooldown", true),
            new ShapeRef("charges", "charges", true)
        )),
        new RefSection("gui.runicrebirth.adv_spells", List.of(
            new ShapeRef("binding", "binding", false),
            new ShapeRef("hammer", "hammer", false),
            new ShapeRef("ballista", "ballista", false)
        ), List.of(
            new ShapeRef("plus_four", "plus_four", true),
            new ShapeRef("four_casts", "four_casts", true)
        ))
    );

    private record RadialSlot(ShapeRef shape, double angle, float targetX, float targetY) {}

    private final StrokeBuffer buffer = new StrokeBuffer();
    private boolean drawing;
    private boolean terminalSent;
    private double lastParticleX, lastParticleY;

    private int canvasCenterX, canvasCenterY;
    private int drawRadius;

    private final List<Element> elements = new ArrayList<>();
    private int selectedElementIdx;
    private ShapeRef selectedRef;

    private int openRadialTier = -1;
    private float radialAnimProgress = 0f;
    private List<RadialSlot> activeRadialSlots = new ArrayList<>();

    public DrawingCanvasScreen() {
        super(Component.translatable("screen.runicrebirth.drawing_canvas"));
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

        canvasCenterX = (int) (width * DRAWING_X_FRAC);
        canvasCenterY = (int) (height * DRAWING_Y_FRAC);
        drawRadius = (int) (height * DRAWING_RADIUS_FRAC);

        ClientMagicData.setOnStackChanged(this::onStackUpdated);
    }

    private Element selectedElement() {
        return elements.isEmpty() ? null : elements.get(selectedElementIdx);
    }

    private int strokeColor() {
        Element e = selectedElement();
        return e != null ? (0xFF000000 | e.displayColor()) : 0xFFAE78FF;
    }

    private int elementScreenX(int idx) {
        return (int) (width * ELEMENT_X_FRAC[idx]);
    }

    private int elementScreenY(int idx) {
        return (int) (height * ELEMENT_Y_FRACS[idx]);
    }

    private int tierScreenX(int idx) {
        return (int) (width * TIER_X_FRAC[idx]);
    }

    private int tierScreenY(int idx) {
        return (int) (height * TIER_Y_FRACS[idx]);
    }

    private int elementHitRadius() {
        return (int) (height * ELEMENT_HIT_FRAC);
    }

    private int tierHitRadius() {
        return (int) (height * TIER_HIT_FRAC);
    }

    private int tierSelectedX() {
        return (int) (width * TIER_SELECTED_X);
    }

    private int tierSelectedY() {
        return (int) (height * TIER_SELECTED_Y);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        //renderDebugZones(g);

        if (selectedRef != null) renderReferenceOverlay(g);
        //renderStrokes(g);
        renderRadialMenu(g);

        g.drawCenteredString(font, Component.translatable("screen.runicrebirth.canvas_hint"),
            width / 2, 4, 0xAAFFFFFF);

        RenderSystem.disableBlend();
    }

    private void renderReferenceOverlay(GuiGraphics g) {
        int overlaySize = drawRadius;
        int ox = canvasCenterX - overlaySize / 2;
        int oy = canvasCenterY - overlaySize / 2;
        int src = selectedRef.iconSize();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.blit(selectedRef.iconPath(), ox, oy, overlaySize, overlaySize,
            0, 0, src, src, src, src);
        RenderSystem.disableBlend();
    }

    private void renderStrokes(GuiGraphics g) {
        int color = strokeColor();
        int offsetX = canvasCenterX - drawRadius;
        int offsetY = canvasCenterY - drawRadius;
        for (List<StrokePoint> stroke : buffer.strokes()) {
            for (int i = 1; i < stroke.size(); i++) {
                StrokePoint a = stroke.get(i - 1);
                StrokePoint b = stroke.get(i);
                drawLine(g, offsetX + a.x(), offsetY + a.y(),
                    offsetX + b.x(), offsetY + b.y(), color);
            }
        }
    }

    private void renderRadialMenu(GuiGraphics g) {
        if (openRadialTier < 0 || activeRadialSlots.isEmpty()) return;

        radialAnimProgress = Math.min(1.0f, radialAnimProgress + RADIAL_ANIM_SPEED);
        float t = 1f - (1f - radialAnimProgress) * (1f - radialAnimProgress);
        if (radialAnimProgress <= 0.4f) return;

        float selCX = tierSelectedX();
        float selCY = tierSelectedY();
        int spellIconSize = width / 30;
        int modIconSize = spellIconSize / 2;

        for (RadialSlot slot : activeRadialSlots) {
            float cx = selCX + (slot.targetX - selCX) * t;
            float cy = selCY + (slot.targetY - selCY) * t;

            int iconSize = slot.shape.isModifier() ? modIconSize : spellIconSize;
            int bgRadius = iconSize / 2 + 4;
            drawFilledCircle(g, (int) cx, (int) cy, bgRadius, RADIAL_BG);

            int src = slot.shape.iconSize();
            int ix = (int) cx - iconSize / 2;
            int iy = (int) cy - iconSize / 2;
            g.blit(slot.shape.iconPath(), ix, iy, iconSize, iconSize,
                0, 0, src, src, src, src);

            if (selectedRef != null && selectedRef.shapeId().equals(slot.shape.shapeId())) {
                drawCircle(g, (int) cx, (int) cy, bgRadius, RADIAL_HIGHLIGHT);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (openRadialTier >= 0 && tryClickRadialSlot(mx, my)) return true;
            if (tryClickElement(mx, my)) return true;
            if (tryClickTierCircle(mx, my)) return true;

            if (insideDrawCircle(mx, my)) {
                drawing = true;
                buffer.beginStroke();
                int offsetX = canvasCenterX - drawRadius;
                int offsetY = canvasCenterY - drawRadius;
                double sx = mx - offsetX;
                double sy = my - offsetY;
                buffer.appendPoint(sx, sy, 0);
                spawnInkParticle(sx, sy);
                lastParticleX = sx;
                lastParticleY = sy;
                return true;
            }

            if (openRadialTier >= 0) {
                closeTierRadial();
                return true;
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            submitDrawing();
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
            int offsetX = canvasCenterX - drawRadius;
            int offsetY = canvasCenterY - drawRadius;
            double sx = mx - offsetX;
            double sy = my - offsetY;
            buffer.appendPoint(sx, sy, 3.0);
            double ddx = sx - lastParticleX;
            double ddy = sy - lastParticleY;
            if (ddx * ddx + ddy * ddy >= 27.0) {
                spawnInkParticle(sx, sy);
                lastParticleX = sx;
                lastParticleY = sy;
            }
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

    private boolean insideDrawCircle(double mx, double my) {
        double dx = mx - canvasCenterX;
        double dy = my - canvasCenterY;
        return dx * dx + dy * dy <= (double) drawRadius * drawRadius;
    }

    private boolean tryClickElement(double mx, double my) {

        int hitR = elementHitRadius();
        double hitR2 = (double) hitR * hitR;

        for (int i = 0; i < ELEMENT_IDS.length && i < ELEMENT_Y_FRACS.length; i++) {
            int ex = elementScreenX(i);
            int ey = elementScreenY(i);
            double dx = mx - ex;
            double dy = my - ey;
            if (dx * dx + dy * dy <= hitR2) {
                ResourceLocation elementId = ELEMENT_ID_MAP.get(ELEMENT_IDS[i]);
                if (elementId == null) continue;

                for (int j = 0; j < elements.size(); j++) {
                    if (elements.get(j).id().equals(elementId)) {
                        selectedElementIdx = j;
                        PacketDistributor.sendToServer(new CanvasSelectElementC2SPacket(elementId));
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean tryClickTierCircle(double mx, double my) {
        if (openRadialTier >= 0) return false;

        int hitR = tierHitRadius();
        double hitR2 = (double) hitR * hitR;

        for (int tier = 0; tier < TIER_Y_FRACS.length; tier++) {
            int tx = tierScreenX(tier);
            int ty = tierScreenY(tier);
            double dx = mx - tx;
            double dy = my - ty;
            if (dx * dx + dy * dy <= hitR2) {
                openRadialTier = tier;
                radialAnimProgress = 0f;
                populateRadialSlots(tier);
                PacketDistributor.sendToServer(new CanvasSelectTierC2SPacket(tier));
                return true;
            }
        }
        return false;
    }

    private void closeTierRadial() {
        openRadialTier = -1;
        radialAnimProgress = 0f;
        activeRadialSlots.clear();
        PacketDistributor.sendToServer(new CanvasSelectTierC2SPacket(-1));
    }

    private void populateRadialSlots(int tier) {
        activeRadialSlots = new ArrayList<>();
        if (tier >= SECTIONS.size()) return;
        RefSection section = SECTIONS.get(tier);
        float spellRadius = height / 6f;
        float modifierRadius = spellRadius * 0.6f;
        float cx = tierSelectedX();
        float cy = tierSelectedY();

        double spellStep = Math.min(45.0, 360.0 / Math.max(1, section.spells().size()));
        double spellStart = -90.0 - spellStep * (section.spells().size() - 1) / 2.0;
        for (int i = 0; i < section.spells().size(); i++) {
            double angle = Math.toRadians(spellStart + i * spellStep);
            float targetX = cx + (float) (spellRadius * Math.cos(angle));
            float targetY = cy + (float) (spellRadius * Math.sin(angle));
            activeRadialSlots.add(new RadialSlot(section.spells().get(i), angle, targetX, targetY));
        }

        double modStep = Math.min(45.0, 360.0 / Math.max(1, section.modifiers().size()));
        double modStart = -90.0 - modStep * (section.modifiers().size() - 1) / 2.0;
        for (int i = 0; i < section.modifiers().size(); i++) {
            double angle = Math.toRadians(modStart + i * modStep);
            float targetX = cx + (float) (modifierRadius * Math.cos(angle));
            float targetY = cy + (float) (modifierRadius * Math.sin(angle));
            activeRadialSlots.add(new RadialSlot(section.modifiers().get(i), angle, targetX, targetY));
        }
    }

    private boolean tryClickRadialSlot(double mx, double my) {
        if (openRadialTier < 0 || activeRadialSlots.isEmpty()) return false;

        float selCX = tierSelectedX();
        float selCY = tierSelectedY();

        float t = 1f - (1f - radialAnimProgress) * (1f - radialAnimProgress);
        int iconDisplaySize = width / 30;
        float hitRadius = iconDisplaySize / 2f + 4;

        for (RadialSlot slot : activeRadialSlots) {
            float cx = selCX + (slot.targetX - selCX) * t;
            float cy = selCY + (slot.targetY - selCY) * t;
            double dx = mx - cx;
            double dy = my - cy;
            if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                if (selectedRef != null && selectedRef.shapeId().equals(slot.shape.shapeId())) {
                    selectedRef = null;
                } else {
                    selectedRef = slot.shape;
                }
                return true;
            }
        }
        return false;
    }

    private void submitDrawing() {
        if (drawing) {
            buffer.endStroke();
            drawing = false;
        }
        if (buffer.isEmpty() || buffer.totalPoints() < 4) {
            buffer.clear();
            InkParticle.removeAll();
            return;
        }
        Element el = selectedElement();
        ResourceLocation elemId = el != null ? el.id()
            : ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "arcane");
        PacketDistributor.sendToServer(new DrawSubmitC2SPacket(buffer.snapshot(), elemId));
        buffer.clear();
        InkParticle.removeAll();
    }

    private void onStackUpdated() {
        if (ClientMagicData.isActiveStackValid()) {
            if (Minecraft.getInstance().player != null) this.onClose();
        }
    }

    private void cancelAndClose() {
        if (terminalSent) return;
        terminalSent = true;
        PacketDistributor.sendToServer(new CancelDrawC2SPacket());
        this.onClose();
    }

    @Override
    public void onClose() {
        InkParticle.removeAll();
        ClientMagicData.clearOnStackChanged();
        if (!terminalSent) {
            terminalSent = true;
            PacketDistributor.sendToServer(new CancelDrawC2SPacket());
        }
        super.onClose();
    }

    private void renderDebugZones(GuiGraphics g) {
        int elemR = elementHitRadius();

        drawCircle(g, canvasCenterX, canvasCenterY, drawRadius, CANVAS_BORDER);

        for (int i = 0; i < ELEMENT_Y_FRACS.length; i++) {
            int ex = elementScreenX(i);
            int ey = elementScreenY(i);
            drawCircle(g, ex, ey, elemR, 0xFF00FF00);
            g.drawString(font, ELEMENT_IDS[i], ex + elemR + 2, ey - 4, 0xFF00FF00, false);
        }

        int tierR = tierHitRadius();

        String[] tierNames = {"basic", "inter", "adv"};
        for (int i = 0; i < TIER_Y_FRACS.length; i++) {
            int tx = tierScreenX(i);
            int ty = tierScreenY(i);
            drawCircle(g, tx, ty, tierR, 0xFFFF8800);
            g.drawString(font, tierNames[i], tx - tierR - font.width(tierNames[i]) - 2, ty - 4, 0xFFFF8800, false);
        }
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

    private static void drawFilledCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            int halfW = (int) Math.round(Math.sqrt((double) radius * radius - (double) dy * dy));
            g.fill(cx - halfW, cy + dy, cx + halfW, cy + dy + 1, color);
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

    private void spawnInkParticle(double strokeX, double strokeY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        double nx = (strokeX / drawRadius) - 1.0;
        double ny = -((strokeY / drawRadius) - 1.0);

        double localX = nx * INK_WORLD_RADIUS;
        double localY = ny * INK_WORLD_RADIUS;

        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();
        float yawRad = (float) Math.toRadians(180.0 - yaw);
        float pitchRad = (float) Math.toRadians(-pitch);
        float cosY = Mth.cos(yawRad);
        float sinY = Mth.sin(yawRad);
        float cosP = Mth.cos(pitchRad);
        float sinP = Mth.sin(pitchRad);

        double ry = localY * cosP;
        double rz = localY * sinP;

        double wx = localX * cosY + rz * sinY;
        double wy = ry;
        double wz = -localX * sinY + rz * cosY;

        Vec3 eye = mc.player.getEyePosition(1.0f);
        Vec3 look = mc.player.getLookAngle();
        double px = eye.x + look.x * INK_PLANE_DISTANCE + wx;
        double py = eye.y + look.y * INK_PLANE_DISTANCE + wy;
        double pz = eye.z + look.z * INK_PLANE_DISTANCE + wz;

        mc.level.addParticle(getInkParticle(), px, py, pz, 0, 0, 0);
    }

    private ParticleOptions getInkParticle() {
        Element e = selectedElement();
        if (e == null) return new ScaledParticleOption(ModParticles.ARCANE_INK.get(), 1.0f);
        String id = e.id().getPath();
        return switch (id) {
            case "fire" -> new ScaledParticleOption(ModParticles.FIRE_INK.get(), 1.0f);
            case "ice" -> new ScaledParticleOption(ModParticles.ICE_INK.get(), 1.0f);
            case "earth" -> new ScaledParticleOption(ModParticles.EARTH_INK.get(), 1.0f);
            case "wind" -> new ScaledParticleOption(ModParticles.WIND_INK.get(), 1.0f);
            default -> new ScaledParticleOption(ModParticles.ARCANE_INK.get(), 1.0f);
        };
    }
}
