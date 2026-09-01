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
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class DrawingCanvasScreen extends Screen {

    private static final int CANVAS_BORDER = 0xFF605040;
    private static final int RADIAL_BG = 0x73FFFFFF;
    private static final int RADIAL_HIGHLIGHT = 0xFFDBA914;
    private static final float RADIAL_ANIM_SPEED = 0.01f;
    private static final double INK_WORLD_RADIUS = 0.3;
    private static final float INK_BASE_DISTANCE = 0.7f;
    private static final float INK_DEFAULT_FOV = 70.0f;

    private static final float[] ELEMENT_X_FRAC = {0.27f, 0.21f, 0.19f, 0.225f, 0.30f};
    private static final float[] ELEMENT_Y_FRACS = {0.19f, 0.32f, 0.53f, 0.705f, 0.83f};
    private static final String[] ELEMENT_IDS = {"arcane", "fire", "ice", "earth", "wind"};

    private static final float DRAWING_X_FRAC = 0.5f;
    private static final float DRAWING_Y_FRAC = 0.5f;
    private static final float DRAWING_RADIUS_FRAC = 0.3f;

    private static final float[] TIER_X_FRAC = {0.813f, 0.84f, 0.81f};
    private static final float[] TIER_Y_FRACS = {0.18f, 0.46f, 0.785f};

    private static final float TIER_SELECTED_X = 0.847f;
    private static final float TIER_SELECTED_Y = 0.5f;

    private static final float ELEMENT_HIT_FRAC = 0.065f;
    private static final float TIER_HIT_FRAC = 0.085f;

    private static final Map<String, Identifier> ELEMENT_ID_MAP = Map.of(
        "arcane", Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "arcane"),
        "fire", Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "fire"),
        "ice", Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "ice"),
        "earth", Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "earth"),
        "wind", Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "wind")
    );

    private static final Identifier LOCKED_OVERLAY = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/gui/locked_spell_overlay.png");

    private static final Identifier SLOT_SMALL_FILLED =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border_small");
    private static final Identifier SLOT_SMALL_UNAVAIL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border_small_unavail");
    private static final Identifier SLOT_BIG =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border");
    private static final Identifier SLOT_BIG_SELECTED =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border_selected");

    private record ShapeRef(String shapeId, String iconName, boolean isModifier, String spellTypeId, String displayOverride) {
        ShapeRef(String shapeId, String iconName, boolean isModifier) {
            this(shapeId, iconName, isModifier, null, null);
        }
        ShapeRef(String shapeId, String iconName, boolean isModifier, String spellTypeId) {
            this(shapeId, iconName, isModifier, spellTypeId, null);
        }
        Identifier iconPath() {
            String suffix = isModifier ? "_icon_small_outline" : "_icon_outline";
            return Identifier.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/gui/shape_icons/" + iconName + suffix + ".png");
        }
        Identifier actualIconPath() {
            String suffix = isModifier ? "_icon_small" : "_icon";
            return Identifier.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/gui/shape_icons/" + iconName + suffix + ".png");
        }
        int iconSize() { return isModifier ? 11 : 19; }
        boolean isLocked() {
            if (spellTypeId == null) return false;
            return !ClientMagicData.isSpellUnlocked(Identifier.parse(spellTypeId));
        }
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
      return;
    }

  private record RefSection(String titleKey, List<ShapeRef> spells, List<ShapeRef> modifiers) {}

    private static final List<RefSection> SECTIONS = List.of(
        new RefSection("gui.runicrebirth.beg_spells", List.of(
            new ShapeRef("line_down", "line", false, "runicrebirth:magic_projectile"),
            new ShapeRef("circle", "circle", false, "runicrebirth:magic_blast"),
            new ShapeRef("v_shape", "v", false, "runicrebirth:magic_beam"),
            new ShapeRef("arrow", "arrow", false, "runicrebirth:magic_arrow"),
            new ShapeRef("infusion", "infusion", false, "runicrebirth:infusion")
        ), List.of(
            new ShapeRef("plus", "plus", true),
            new ShapeRef("range", "range", true),
            new ShapeRef("two_casts", "two_casts", true),
            new ShapeRef("sharp_boost", "sharp_boost", true),
            new ShapeRef("blunt_boost", "blunt_boost", true),
            new ShapeRef("magic_boost", "magic_boost", true),
            new ShapeRef("charges", "charges", true, null, "2x Charges")
        )),
        new RefSection("gui.runicrebirth.int_spells", List.of(
            new ShapeRef("shield", "shield", false, "runicrebirth:magic_shield"),
            new ShapeRef("slash", "slash", false, "runicrebirth:magic_slash"),
            new ShapeRef("explosion", "explosion", false, "runicrebirth:magic_explosion"),
            new ShapeRef("meteor", "meteor", false, "runicrebirth:magic_meteor")
        ), List.of(
            new ShapeRef("plus_two", "plus_two", true),
            new ShapeRef("cooldown", "cooldown", true),
            new ShapeRef("charges_three", "three_charges", true, null, "3x Charges")
        )),
        new RefSection("gui.runicrebirth.adv_spells", List.of(
            new ShapeRef("binding", "binding", false, "runicrebirth:magic_binding"),
            new ShapeRef("hammer", "hammer", false, "runicrebirth:magic_hammer"),
            new ShapeRef("ballista", "ballista", false, "runicrebirth:magic_ballista")
        ), List.of(
            new ShapeRef("plus_four", "plus_four", true),
            new ShapeRef("four_casts", "four_casts", true),
            new ShapeRef("charges_four", "four_charges", true, null, "4x Charges")
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
    private CanvasAmbientSound ambientSound;

    private final boolean circuitMode;
    private final int circuitModifierSlots;
    private final java.util.List<com.github.runicrebirth.api.spells.WandStacksData.ComponentRef> pendingCircuitRefs = new ArrayList<>();

    public DrawingCanvasScreen() {
        this(false, 0);
    }

    public DrawingCanvasScreen(boolean circuitMode, int circuitModifierSlots) {
        super(Component.translatable("screen.runicrebirth.drawing_canvas"));
        this.circuitMode = circuitMode;
        this.circuitModifierSlots = circuitModifierSlots;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        elements.clear();
        ElementRegistry.REGISTRY.forEach(elements::add);
        if (selectedElementIdx >= elements.size()) selectedElementIdx = 0;

        canvasCenterX = (int) (width * DRAWING_X_FRAC);
        canvasCenterY = (int) (height * DRAWING_Y_FRAC);
        drawRadius = (int) (height * DRAWING_RADIUS_FRAC);

        ClientMagicData.setOnStackChanged(this::onStackUpdated);

        ambientSound = new CanvasAmbientSound();
        Minecraft.getInstance().getSoundManager().play(ambientSound);
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
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        //renderDebugZones(g);

        if (selectedRef != null && !selectedRef.isLocked()) renderReferenceOverlay(g);
        //renderStrokes(g);
        renderRadialMenu(g);
        renderInscribedSlots(g);

        g.centeredText(font != null ? font : Minecraft.getInstance().font,
            Component.translatable("screen.runicrebirth.canvas_hint"),
            width / 2, 4, 0xAAFFFFFF);

        renderHoverTooltip(g, mouseX, mouseY);
    }

    private void renderReferenceOverlay(GuiGraphicsExtractor g) {
        int overlaySize = drawRadius;
        int ox = canvasCenterX - overlaySize / 2;
        int oy = canvasCenterY - overlaySize / 2;
        int src = selectedRef.iconSize();

        g.blit(RenderPipelines.GUI_TEXTURED, selectedRef.iconPath(), ox, oy, 0f, 0f,
            overlaySize, overlaySize, src, src, src, src);
    }

    private void renderStrokes(GuiGraphicsExtractor g) {
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

    private void renderRadialMenu(GuiGraphicsExtractor g) {
        if (openRadialTier < 0 || activeRadialSlots.isEmpty()) return;

        radialAnimProgress = Math.min(1.0f, radialAnimProgress + RADIAL_ANIM_SPEED);
        float t = 1f - (1f - radialAnimProgress) * (1f - radialAnimProgress);
        if (radialAnimProgress <= 0.4f) return;

        float selCX = tierSelectedX();
        float selCY = tierSelectedY();
        int spellIconSize = width / 25;
        int modIconSize = spellIconSize / 2;

        for (RadialSlot slot : activeRadialSlots) {
            float cx = selCX + (slot.targetX - selCX) * t;
            float cy = selCY + (slot.targetY - selCY) * t;

            int iconSize = slot.shape.isModifier() ? modIconSize : spellIconSize;
            int ix = (int) cx - iconSize / 2;
            int iy = (int) cy - iconSize / 2;
            boolean isSelected = selectedRef != null && selectedRef.shapeId().equals(slot.shape.shapeId());

            if (slot.shape.isModifier()) {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, isSelected ? SLOT_SMALL_FILLED : SLOT_SMALL_UNAVAIL, ix, iy, iconSize, iconSize);
            } else {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BIG, ix, iy, iconSize, iconSize);
                if (isSelected) g.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BIG_SELECTED, ix, iy, iconSize, iconSize);
            }

            int src = slot.shape.iconSize();
            int drawSize = (int) (iconSize * 0.7f);
            int iconX = ix + (iconSize - drawSize) / 2;
            int iconY = iy + (iconSize - drawSize) / 2;
            g.blit(RenderPipelines.GUI_TEXTURED, slot.shape.actualIconPath(), iconX, iconY, 0f, 0f,
                drawSize, drawSize, src, src, src, src);

            if (slot.shape.isLocked()) {
                int lockSize = iconSize;
                int lx = (int) cx - lockSize / 2;
                int ly = (int) cy - lockSize / 2;
                g.blit(RenderPipelines.GUI_TEXTURED, LOCKED_OVERLAY, lx, ly, 0f, 0f,
                    lockSize, lockSize, 32, 32, 32, 32);
            }
        }
    }

    private void renderInscribedSlots(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();

        java.util.List<com.github.runicrebirth.api.spells.WandStacksData.ComponentRef> refs = null;
        int totalSlots = 0;

        if (circuitMode) {
            totalSlots = circuitModifierSlots;
            if (!pendingCircuitRefs.isEmpty()) {
                refs = pendingCircuitRefs;
            }
        } else if (!circuitMode && held.getItem() instanceof com.github.runicrebirth.items.SpellWriter) {
            var wandData = held.get(com.github.runicrebirth.init.ModDataComponents.WAND_STACKS.get());
            if (wandData != null && !wandData.stacks().isEmpty()) {
                var entry = wandData.stacks().get(wandData.activeIndex());
                if (!entry.components().isEmpty()) refs = entry.components();
                totalSlots = com.github.runicrebirth.items.SpellWriter.getMaxModifierSlots(held);
            }
        }

        if (totalSlots == 0) return;

        int displayCount = totalSlots;
        int iconSize = 11;
        int gap = 2;
        int totalW = displayCount * iconSize + (displayCount - 1) * gap;
        int startX = width / 2 - totalW / 2;
        int y = 16;

        Identifier slotBg = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border_small");
        Identifier emptySlot = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/overlay_slot_border_small_unavail");

        int filledCount = refs != null ? refs.size() : 0;
        for (int i = 0; i < displayCount; i++) {
            int x = startX + i * (iconSize + gap);
            if (i < filledCount) {
                var ref = refs.get(i);
                com.github.runicrebirth.api.spells.SpellComponent comp = ref.kind() == com.github.runicrebirth.api.spells.WandStacksData.ComponentRef.KIND_TYPE
                    ? com.github.runicrebirth.api.registry.SpellTypeRegistry.get(ref.id())
                    : com.github.runicrebirth.api.registry.ModifierRegistry.get(ref.id());
                g.blitSprite(RenderPipelines.GUI_TEXTURED, comp != null ? comp.getOverlaySlotPath() : slotBg, x, y, iconSize, iconSize);
                if (comp != null) {
                    g.blit(RenderPipelines.GUI_TEXTURED, comp.getSpellIconPath(), x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
                }
            } else {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, emptySlot, x, y, iconSize, iconSize);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        double mx = event.x();
        double my = event.y();
        int button = event.button();
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
        return super.mouseClicked(event, consumed);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        double mx = event.x();
        double my = event.y();
        int button = event.button();
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
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && drawing) {
            buffer.endStroke();
            drawing = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { cancelAndClose(); return true; }
        if (ModKeyMappings.isMovementKey(keyCode, scanCode)) { cancelAndClose(); return true; }
        return super.keyPressed(event);
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
                Identifier elementId = ELEMENT_ID_MAP.get(ELEMENT_IDS[i]);
                if (elementId == null) continue;

                for (int j = 0; j < elements.size(); j++) {
                    if (elements.get(j).id().equals(elementId)) {
                        selectedElementIdx = j;
                        ClientPacketDistributor.sendToServer(new CanvasSelectElementC2SPacket(elementId));
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
                ClientPacketDistributor.sendToServer(new CanvasSelectTierC2SPacket(tier));
                return true;
            }
        }
        return false;
    }

    private void closeTierRadial() {
        openRadialTier = -1;
        radialAnimProgress = 0f;
        activeRadialSlots.clear();
        ClientPacketDistributor.sendToServer(new CanvasSelectTierC2SPacket(-1));
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
                if (slot.shape.isLocked()) return true;
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
            if (circuitMode) {
                ClientPacketDistributor.sendToServer(new com.github.runicrebirth.network.FinalizeCircuitC2SPacket());
                this.onClose();
            }
            return;
        }
        Element el = selectedElement();
        Identifier elemId = el != null ? el.id()
            : Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "arcane");

        String hintShapeId = selectedRef != null
            ? RunicRebirth.MODID + ":" + selectedRef.shapeId()
            : null;

        if (circuitMode && pendingCircuitRefs.size() < circuitModifierSlots) {
            var recognizer = com.github.runicrebirth.magic.recognition.Recognizers.get();
            var result = recognizer.recognizeStrokes(buffer.snapshot(), hintShapeId, 0.65);
            if (result != null && result.id() != null) {
                Identifier shapeId = Identifier.parse(result.id());
                double threshold = com.github.runicrebirth.api.registry.ShapeRegistry.thresholdFor(shapeId);
                if (result.score() / 10 >= threshold) {
                    com.github.runicrebirth.api.spells.SpellComponent comp =
                        com.github.runicrebirth.api.registry.ShapeRegistry.componentFor(shapeId);
                    if (comp != null) {
                        int kind = comp instanceof com.github.runicrebirth.api.spells.SpellType
                            ? com.github.runicrebirth.api.spells.WandStacksData.ComponentRef.KIND_TYPE
                            : com.github.runicrebirth.api.spells.WandStacksData.ComponentRef.KIND_MODIFIER;
                        pendingCircuitRefs.add(new com.github.runicrebirth.api.spells.WandStacksData.ComponentRef(kind, comp.id()));
                    }
                }
            }
        }

        ClientPacketDistributor.sendToServer(new DrawSubmitC2SPacket(buffer.snapshot(), elemId, hintShapeId));
        buffer.clear();
        InkParticle.removeAll();
    }

    private void onStackUpdated() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (circuitMode) return;

        if (ClientMagicData.isActiveStackValid()) {
            this.onClose();
        }
    }

    private void cancelAndClose() {
        if (terminalSent) return;
        terminalSent = true;
        ClientPacketDistributor.sendToServer(new CancelDrawC2SPacket());
        this.onClose();
    }

    @Override
    public void removed() {
        stopAmbientSound();
        super.removed();
    }

    @Override
    public void onClose() {
        stopAmbientSound();
        InkParticle.removeAll();
        ClientMagicData.clearOnStackChanged();
        if (!terminalSent) {
            terminalSent = true;
            ClientPacketDistributor.sendToServer(new CancelDrawC2SPacket());
        }
        super.onClose();
    }

    private void stopAmbientSound() {
        if (ambientSound != null) {
            Minecraft.getInstance().getSoundManager().stop(ambientSound);
            ambientSound = null;
        }
    }

    private static final String[] ELEMENT_DISPLAY_NAMES = {"Arcane", "Fire", "Ice", "Earth", "Wind"};
    private static final String[] TIER_DISPLAY_NAMES = {"Acolyte Tier", "Adept Tier", "Arch Tier"};
    private static final int[] TIER_COLORS = {0xFFCD7F32, 0xFFC0C0C0, 0xFFFFD700};

    private void renderHoverTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        String text = null;
        int color = 0xFFFFFFFF;

        // Element zones
        int elemHitR = elementHitRadius();
        double elemHitR2 = (double) elemHitR * elemHitR;
        for (int i = 0; i < ELEMENT_IDS.length && i < ELEMENT_Y_FRACS.length; i++) {
            int ex = elementScreenX(i);
            int ey = elementScreenY(i);
            double dx = mouseX - ex;
            double dy = mouseY - ey;
            if (dx * dx + dy * dy <= elemHitR2) {
                Identifier elemId = ELEMENT_ID_MAP.get(ELEMENT_IDS[i]);
                int elemColor = 0xFFAE78FF;
                if (elemId != null) {
                    for (Element e : elements) {
                        if (e.id().equals(elemId)) { elemColor = 0xFF000000 | e.displayColor(); break; }
                    }
                }
                text = ELEMENT_DISPLAY_NAMES[i] + " Element";
                color = elemColor;
                break;
            }
        }

        // Tier zones (only when radial closed)
        if (text == null && openRadialTier < 0) {
            int tierHitR = tierHitRadius();
            double tierHitR2 = (double) tierHitR * tierHitR;
            for (int tier = 0; tier < TIER_Y_FRACS.length; tier++) {
                int tx = tierScreenX(tier);
                int ty = tierScreenY(tier);
                double dx = mouseX - tx;
                double dy = mouseY - ty;
                if (dx * dx + dy * dy <= tierHitR2) {
                    text = TIER_DISPLAY_NAMES[tier];
                    color = TIER_COLORS[tier];
                    break;
                }
            }
        }

        // Radial menu slots
        if (text == null && openRadialTier >= 0 && !activeRadialSlots.isEmpty()) {
            float selCX = tierSelectedX();
            float selCY = tierSelectedY();
            float t = 1f - (1f - radialAnimProgress) * (1f - radialAnimProgress);
            int iconDisplaySize = width / 30;
            float hitRadius = iconDisplaySize / 2f + 4;
            for (RadialSlot slot : activeRadialSlots) {
                float cx = selCX + (slot.targetX - selCX) * t;
                float cy = selCY + (slot.targetY - selCY) * t;
                double dx = mouseX - cx;
                double dy = mouseY - cy;
                if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                    if (slot.shape.spellTypeId() != null) {
                        com.github.runicrebirth.api.spells.SpellComponent comp =
                            com.github.runicrebirth.api.registry.SpellTypeRegistry.get(Identifier.parse(slot.shape.spellTypeId()));
                        text = comp != null ? comp.displayName().getString() : formatId(slot.shape.spellTypeId());
                    } else if (slot.shape.displayOverride() != null) {
                        text = slot.shape.displayOverride();
                    } else {
                        text = formatId(slot.shape.shapeId());
                    }
                    color = 0xFFFFFFFF;
                    break;
                }
            }
        }

        // Inscribed modifier slots
        if (text == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();
                java.util.List<com.github.runicrebirth.api.spells.WandStacksData.ComponentRef> refs = null;
                int totalSlots = 0;
                if (circuitMode) {
                    totalSlots = circuitModifierSlots;
                    if (!pendingCircuitRefs.isEmpty()) refs = pendingCircuitRefs;
                } else if (held.getItem() instanceof com.github.runicrebirth.items.SpellWriter) {
                    var wandData = held.get(com.github.runicrebirth.init.ModDataComponents.WAND_STACKS.get());
                    if (wandData != null && !wandData.stacks().isEmpty()) {
                        var entry = wandData.stacks().get(wandData.activeIndex());
                        if (!entry.components().isEmpty()) refs = entry.components();
                        totalSlots = com.github.runicrebirth.items.SpellWriter.getMaxModifierSlots(held);
                    }
                }
                if (totalSlots > 0 && refs != null) {
                    int iconSize = 11;
                    int gap = 2;
                    int totalW = totalSlots * iconSize + (totalSlots - 1) * gap;
                    int startX = width / 2 - totalW / 2;
                    int slotY = 16;
                    int filledCount = refs.size();
                    for (int i = 0; i < Math.min(filledCount, totalSlots); i++) {
                        int sx = startX + i * (iconSize + gap);
                        if (mouseX >= sx && mouseX < sx + iconSize && mouseY >= slotY && mouseY < slotY + iconSize) {
                            var ref = refs.get(i);
                            com.github.runicrebirth.api.spells.SpellComponent comp =
                                ref.kind() == com.github.runicrebirth.api.spells.WandStacksData.ComponentRef.KIND_TYPE
                                ? com.github.runicrebirth.api.registry.SpellTypeRegistry.get(ref.id())
                                : com.github.runicrebirth.api.registry.ModifierRegistry.get(ref.id());
                            text = comp != null ? comp.displayName().getString() : formatId(ref.id().getPath());
                            color = 0xFFFFFFFF;
                            break;
                        }
                    }
                }
            }
        }

        if (text == null) return;

        int tw = font.width(text);
        int tx = mouseX + 10;
        int ty = mouseY - font.lineHeight - 2;
        if (tx + tw + 4 > width) tx = mouseX - tw - 10;
        if (ty < 0) ty = mouseY + 4;
        g.fill(tx - 3, ty - 2, tx + tw + 3, ty + font.lineHeight + 2, 0xC0000000);
        g.text(font, text, tx, ty, color, false);
    }

    private static String formatId(String id) {
        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return sb.toString();
    }

    private void renderDebugZones(GuiGraphicsExtractor g) {
        int elemR = elementHitRadius();

        drawCircle(g, canvasCenterX, canvasCenterY, drawRadius, CANVAS_BORDER);

        for (int i = 0; i < ELEMENT_Y_FRACS.length; i++) {
            int ex = elementScreenX(i);
            int ey = elementScreenY(i);
            drawCircle(g, ex, ey, elemR, 0xFF00FF00);
            g.text(font, ELEMENT_IDS[i], ex + elemR + 2, ey - 4, 0xFF00FF00, false);
        }

        int tierR = tierHitRadius();

        String[] tierNames = {"basic", "inter", "adv"};
        for (int i = 0; i < TIER_Y_FRACS.length; i++) {
            int tx = tierScreenX(i);
            int ty = tierScreenY(i);
            drawCircle(g, tx, ty, tierR, 0xFFFF8800);
            g.text(font, tierNames[i], tx - tierR - font.width(tierNames[i]) - 2, ty - 4, 0xFFFF8800, false);
        }
    }

    private static void drawCircle(GuiGraphicsExtractor g, int cx, int cy, int radius, int color) {
        int segments = Math.max(64, radius * 2);
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            int px = cx + (int) Math.round(Math.cos(angle) * radius);
            int py = cy + (int) Math.round(Math.sin(angle) * radius);
            g.fill(px, py, px + 1, py + 1, color);
        }
    }

    private static void drawFilledCircle(GuiGraphicsExtractor g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            int halfW = (int) Math.round(Math.sqrt((double) radius * radius - (double) dy * dy));
            g.fill(cx - halfW, cy + dy, cx + halfW, cy + dy + 1, color);
        }
    }

    private static void drawLine(GuiGraphicsExtractor g, double x1, double y1, double x2, double y2, int color) {
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

        float inkFov = (float) mc.options.fov().get();
        double inkRatio = Math.tan(Math.toRadians(INK_DEFAULT_FOV * 0.5)) / Math.tan(Math.toRadians(inkFov * 0.5));
        float inkDist = INK_BASE_DISTANCE * (float) Math.pow(inkRatio, 0.914);

        double lateralScale = inkDist * Math.tan(Math.toRadians(inkFov * 0.5)) * 2.0 * DRAWING_RADIUS_FRAC;
        double localX = nx * lateralScale;
        double localY = ny * lateralScale;

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
        double px = eye.x + look.x * inkDist + wx;
        double py = eye.y + look.y * inkDist + wy;
        double pz = eye.z + look.z * inkDist + wz;

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

    private static class CanvasAmbientSound extends AbstractTickableSoundInstance {
        CanvasAmbientSound() {
            super(ModSounds.CANVAS_AMBIENT.get(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0f;
            this.pitch = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {}
    }
}
