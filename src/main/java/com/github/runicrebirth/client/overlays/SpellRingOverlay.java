package com.github.runicrebirth.client.overlays;

import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.input.ModKeyMappings;
import com.github.runicrebirth.entities.MagicHandEntity;
import com.github.runicrebirth.entities.spells.ArcaneTetherEntity;
import com.github.runicrebirth.items.curios.ArcaneTetherRingItem;
import com.github.runicrebirth.items.curios.BlinkRingItem;
import com.github.runicrebirth.items.curios.HoverRingItem;
import com.github.runicrebirth.items.curios.MagicHandRingItem;
import com.github.runicrebirth.items.curios.RingOfLeapingGalesItem;
import com.github.runicrebirth.items.curios.RingOfPhantomMiningItem;
import com.github.runicrebirth.items.curios.ThrusterRingItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

@OnlyIn(Dist.CLIENT)
public class SpellRingOverlay implements LayeredDraw.Layer {

    public static final SpellRingOverlay INSTANCE = new SpellRingOverlay();

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_GAP = 3;
    private static final int MAX_RINGS = 5;

    // Must match ACTIVATE_SPELL_RINGS index order in ModKeyMappings
    private static final String[] SLOT_IDS = {
        "thumb_spell_ring", "index_spell_ring", "middle_spell_ring",
        "ring_spell_ring", "pinkie_spell_ring"
    };

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        ItemStack[] stacks = getEquippedRings(mc);

        boolean anyEquipped = false;
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) { anyEquipped = true; break; }
        }
        if (!anyEquipped) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int totalWidth = MAX_RINGS * SLOT_SIZE + (MAX_RINGS - 1) * SLOT_GAP;
        int startX = screenWidth / 2 - 120 - 4 - totalWidth;
        int slotY = screenHeight - 22 + 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (int i = 0; i < MAX_RINGS; i++) {
            int x = startX + i * (SLOT_SIZE + SLOT_GAP);
            ItemStack stack = stacks[i];

            drawSlotBackground(graphics, x, slotY, !stack.isEmpty());

            if (!stack.isEmpty()) {
                graphics.renderFakeItem(stack, x + 1, slotY + 1);
                drawDurationBar(graphics, stack, x, slotY);
                drawCooldownBar(graphics, stack, x, slotY);
            }

            drawKeyLabel(graphics, mc, x, slotY, i);
        }

        RenderSystem.disableBlend();
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

    private static String resolveKeyLabel(Minecraft mc, int slotIndex) {
        if (slotIndex >= ModKeyMappings.ACTIVATE_SPELL_RINGS.length) return null;
        InputConstants.Key key = ModKeyMappings.ACTIVATE_SPELL_RINGS[slotIndex].getKey();
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

    private static void drawKeyLabel(GuiGraphics graphics, Minecraft mc, int x, int y, int slotIndex) {
        String keyName = resolveKeyLabel(mc, slotIndex);
        if (keyName == null) return;

        // Half-size text in bottom-left of slot
        float scale = 0.5f;
        int scaledFontH = 4; // 8px * 0.5
        int scaledWidth = (int)(mc.font.width(keyName) * scale);

        int textX = x + 1;
        int textY = y + SLOT_SIZE - scaledFontH - 1;

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(textX, textY, 300f);
        pose.scale(scale, scale, 1f);
        // Small background behind label
        graphics.fill(-(int)(1 / scale), -(int)(1 / scale),
            (int)(mc.font.width(keyName) + 1), scaledFontH * 2 + (int)(1 / scale), 0x99000000);
        graphics.drawString(mc.font, keyName, 0, 0, 0xFFFFFF, false);
        pose.popPose();
    }

    private static void drawDurationBar(GuiGraphics graphics, ItemStack stack, int x, int y) {
        ResourceLocation durId = getDurationId(stack);
        if (durId == null) return;

        int remaining = ClientMagicData.ringDurationRemaining(durId);
        if (remaining == 0) return;

        if (remaining < 0) {
            // Indefinite active state (hover ring on, magic hand passive) — solid teal tint
            graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x4400CCAA);
        } else {
            int max = ClientMagicData.ringDurationMax(durId);
            float fraction = Math.min(1.0f, remaining / (float) Math.max(1, max));
            int filledHeight = (int)(SLOT_SIZE * fraction);
            int elapsedHeight = SLOT_SIZE - filledHeight;
            if (elapsedHeight > 0) graphics.fill(x, y, x + SLOT_SIZE, y + elapsedHeight, 0xAA000000);
            if (filledHeight > 0) graphics.fill(x, y + elapsedHeight, x + SLOT_SIZE, y + SLOT_SIZE, 0x4400CCAA);
        }
    }

    private static void drawCooldownBar(GuiGraphics graphics, ItemStack stack, int x, int y) {
        ResourceLocation cdId = getCooldownId(stack);
        if (cdId == null) return;
        int remaining = ClientMagicData.cooldownRemaining().getOrDefault(cdId, 0);
        if (remaining <= 0) return;
        int max = ClientMagicData.cooldownMax(cdId);
        float fraction = Math.min(1.0f, remaining / (float) max);
        int filledHeight = (int)(SLOT_SIZE * fraction);
        if (filledHeight > 0) {
            // Red overlay from top; bottom edge moves up as cooldown drains
            graphics.fill(x, y, x + SLOT_SIZE, y + filledHeight, 0x99CC2200);
        }
    }

    private static ResourceLocation getDurationId(ItemStack stack) {
        if (stack.getItem() instanceof HoverRingItem) return HoverRingItem.DURATION_KEY;
        if (stack.getItem() instanceof ThrusterRingItem) return ThrusterRingItem.DURATION_KEY;
        if (stack.getItem() instanceof RingOfPhantomMiningItem) return RingOfPhantomMiningItem.DURATION_KEY;
        if (stack.getItem() instanceof MagicHandRingItem) return MagicHandEntity.DURATION_KEY;
        return null;
    }

    private static ResourceLocation getCooldownId(ItemStack stack) {
        if (stack.getItem() instanceof RingOfLeapingGalesItem) return RingOfLeapingGalesItem.COOLDOWN_ID;
        if (stack.getItem() instanceof MagicHandRingItem) return MagicHandEntity.COOLDOWN_KEY;
        if (stack.getItem() instanceof ArcaneTetherRingItem) return ArcaneTetherEntity.COOLDOWN_KEY;
        if (stack.getItem() instanceof BlinkRingItem) return BlinkRingItem.COOLDOWN_ID;
        if (stack.getItem() instanceof ThrusterRingItem) return ThrusterRingItem.COOLDOWN_ID;
        return null;
    }

    private static ItemStack[] getEquippedRings(Minecraft mc) {
        ItemStack[] result = new ItemStack[MAX_RINGS];
        for (int i = 0; i < MAX_RINGS; i++) result[i] = ItemStack.EMPTY;

        if (mc.player == null) return result;

        CuriosApi.getCuriosInventory(mc.player).ifPresent(inv -> {
            var curios = inv.getCurios();
            for (int i = 0; i < SLOT_IDS.length; i++) {
                var handler = curios.get(SLOT_IDS[i]);
                if (handler != null && handler.getStacks().getSlots() > 0) {
                    result[i] = handler.getStacks().getStackInSlot(0);
                }
            }
        });

        return result;
    }
}
