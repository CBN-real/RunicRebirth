package com.github.runicrebirth.client.overlays;

import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.input.ModKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellBladeOverlay implements LayeredDraw.Layer {

    public static final SpellBladeOverlay INSTANCE = new SpellBladeOverlay();

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_GAP = 3;
    // Matches SpellRingOverlay's 5-ring total width for X alignment
    private static final int RING_COUNT = 5;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        ItemStack mainHand = mc.player.getMainHandItem();
        ItemStack offHand = mc.player.getOffhandItem();

        boolean mainIsMagic = mainHand.getItem() instanceof IMagicWeapon;
        boolean offIsMagic = offHand.getItem() instanceof IMagicWeapon;
        if (!mainIsMagic && !offIsMagic) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int ringTotalWidth = RING_COUNT * SLOT_SIZE + (RING_COUNT - 1) * SLOT_GAP;
        int startX = screenWidth / 2 - 120 - 4 - ringTotalWidth;
        int slotY = screenHeight - 22 + 2 - SLOT_SIZE - SLOT_GAP;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Slot 0: off hand
        drawSlot(graphics, mc, startX, slotY, offHand, offIsMagic);
        // Slot 1: main hand
        drawSlot(graphics, mc, startX + SLOT_SIZE + SLOT_GAP, slotY, mainHand, mainIsMagic);
        if (mainIsMagic) {
            drawWeaponKeyLabel(graphics, mc, startX + SLOT_SIZE + SLOT_GAP, slotY);
        }

        RenderSystem.disableBlend();
    }

    private static void drawSlot(GuiGraphics graphics, Minecraft mc, int x, int y,
                                  ItemStack stack, boolean isMagicWeapon) {
        boolean filled = isMagicWeapon && !stack.isEmpty();
        drawSlotBackground(graphics, x, y, filled);

        if (filled) {
            graphics.renderFakeItem(stack, x + 1, y + 1);
            drawCooldownBar(graphics, stack, x, y);
        }
    }

    private static void drawSlotBackground(GuiGraphics graphics, int x, int y, boolean filled) {
        int bg = filled ? 0x88000000 : 0x44000000;
        int borderColor = filled ? 0xFFAAAACC : 0x88888888;
        int borderShadow = filled ? 0xFF8888AA : 0x66666666;

        graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, bg);
        graphics.fill(x, y, x + SLOT_SIZE, y + 1, borderColor);
        graphics.fill(x, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, borderShadow);
        graphics.fill(x, y, x + 1, y + SLOT_SIZE, borderColor);
        graphics.fill(x + SLOT_SIZE - 1, y, x + SLOT_SIZE, y + SLOT_SIZE, borderShadow);
    }

    private static String resolveWeaponKeyLabel(Minecraft mc) {
        InputConstants.Key key = ModKeyMappings.ACTIVATE_WEAPON_ABILITY.getKey();
        if (key.getType() == InputConstants.Type.MOUSE) {
            return switch (key.getValue()) {
                case 0 -> "MBL";
                case 1 -> "MBR";
                case 2 -> "MMB";
                default -> "MB" + (key.getValue() + 1);
            };
        }
        String name = key.getDisplayName().getString();
        if (name.isEmpty()) return null;
        if (mc.font.width(name) > SLOT_SIZE - 2) name = String.valueOf(name.charAt(0));
        return name;
    }

    private static void drawWeaponKeyLabel(GuiGraphics graphics, Minecraft mc, int x, int y) {
        String keyName = resolveWeaponKeyLabel(mc);
        if (keyName == null) return;
        float scale = 0.5f;
        int scaledFontH = 4;
        int textX = x + 1;
        int textY = y + SLOT_SIZE - scaledFontH - 1;
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(textX, textY, 300f);
        pose.scale(scale, scale, 1f);
        graphics.fill(-(int)(1 / scale), -(int)(1 / scale),
            (int)(mc.font.width(keyName) + 1), scaledFontH * 2 + (int)(1 / scale), 0x99000000);
        graphics.drawString(mc.font, keyName, 0, 0, 0xFFFFFF, false);
        pose.popPose();
    }

    private static void drawCooldownBar(GuiGraphics graphics, ItemStack stack, int x, int y) {
        if (!(stack.getItem() instanceof IMagicWeapon weapon)) return;
        ResourceLocation cdId = weapon.getWeaponCooldownId();
        int remaining = ClientMagicData.cooldownRemaining().getOrDefault(cdId, 0);
        if (remaining <= 0) return;
        int max = ClientMagicData.cooldownMax(cdId);
        float fraction = Math.min(1.0f, remaining / (float) Math.max(1, max));
        int filledHeight = (int) (SLOT_SIZE * fraction);
        if (filledHeight > 0) {
            graphics.fill(x, y, x + SLOT_SIZE, y + filledHeight, 0x99CC2200);
        }
    }
}
