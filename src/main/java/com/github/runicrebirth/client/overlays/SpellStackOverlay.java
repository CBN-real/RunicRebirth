package com.github.runicrebirth.client.overlays;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.config.ClientConfig;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.spells.modifiers.ChargesModifier;
import com.github.runicrebirth.spells.modifiers.MultiCastModifier;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * HUD layer rendering all SpellStacks.
 *
 * Active stack renders at full scale at the top, anchored near the lower-left of the screen.
 * Non-active stacks render below the active stack in 2-wide rows at {@value #MINI_SCALE}x scale.
 *
 * Display order is rotated so the active stack is always shown first; other stacks follow
 * in circular order from (active+1) → (active-1). Effect: switching active via keybind makes
 * the previous active visually move to the end of the non-selected list.
 */
@OnlyIn(Dist.CLIENT)
public class SpellStackOverlay implements LayeredDraw.Layer {

    public static final SpellStackOverlay INSTANCE = new SpellStackOverlay();

    private static final int BIG_SIZE = 35;
    private static final int SMALL_SIZE = 11;
    private static final int SELECTED_SIZE = 41;
    private static final int SELECTED_OFFSET = -3;
    private static final int BIG_ICON = 19;
    private static final int BIG_ICON_OFFSET = (BIG_SIZE - BIG_ICON) / 2;
    private static final int SMALL_ICON = 11;

    private static final int SMALL_ROW_GAP = 1;
    private static final int SMALL_COL_GAP = 1;
    private static final int BIG_SMALL_GAP = 4;
    private static final int SMALL_ROWS = 3;

    private static final float MINI_SCALE = 0.25f;
    private static final int MINI_ROW_GAP = 2;
    private static final int MINI_COL_GAP = 4;
    private static final int ACTIVE_TO_MINI_GAP = 4;

    private static final ResourceLocation SLOT_BIG_EMPTY =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "hud/overlay_slot_border");
    private static final ResourceLocation SLOT_SMALL_FILLED =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "hud/overlay_slot_border_small");
    private static final ResourceLocation SLOT_SMALL_UNAVAIL =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "hud/overlay_slot_border_small_unavail");
    private static final ResourceLocation SLOT_SELECTED =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "hud/overlay_slot_border_selected");
    private static final ResourceLocation CHARGE_ICONS =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "textures/gui/sprites/hud/charge_icons.png");
    private static final int CHARGE_ICON_SIZE = 7;
    private static final int CHARGE_ICON_GAP = 1;
    private static final int CHARGE_TEX_SIZE = 16;
    private static final ResourceLocation LOCK_ICON =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "textures/gui/locked_spell_overlay.png");
    private static final int LOCK_SIZE = 7;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!isHoldingSpellWriter(mc)) return;

        ItemStack heldItem = mc.player.getMainHandItem();
        WandStacksData wandData = heldItem.get(ModDataComponents.WAND_STACKS.get());
        if (wandData == null || wandData.stacks().isEmpty()) return;

        List<List<SpellComponent>> stacks = resolveStacks(wandData);
        if (stacks.isEmpty()) return;

        int screenHeight = graphics.guiHeight();
        int active = wandData.activeIndex();
        int maxSmallSlots = ClientConfig.HUD_MAX_SLOTS.get();
        int N = stacks.size();

        int rowLeftX = 10;
        int bottomY = screenHeight - 22;

        // Mini layout geometry
        int miniCount = N - 1;
        int miniRows = (int) Math.ceil(miniCount / 2.0);
        int miniStackW = Math.round(fullRowWidth(maxSmallSlots) * MINI_SCALE);
        int miniStackH = Math.round(BIG_SIZE * MINI_SCALE);
        int miniAreaHeight = miniRows * miniStackH + Math.max(0, miniRows - 1) * MINI_ROW_GAP;

        // Active sits above the mini grid; whole thing is bottom-anchored.
        int activeBottomY = bottomY - miniAreaHeight - (miniRows > 0 ? ACTIVE_TO_MINI_GAP : 0);
        int activeTopY = activeBottomY - BIG_SIZE;
        int miniGridStartY = activeBottomY + ACTIVE_TO_MINI_GAP;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        // Active stack (full size, selected border)
        drawStackRow(graphics, stacks.get(active), rowLeftX, activeTopY, true, maxSmallSlots,
            elementForEntry(wandData, active), wandData.stacks().get(active));

        // Non-active stacks in 2-wide mini rows below active
        for (int i = 0; i < miniCount; i++) {
            int stackIdx = (active + 1 + i) % N;
            int row = i / 2;
            int col = i % 2;
            int miniX = rowLeftX + col * (miniStackW + MINI_COL_GAP);
            int miniY = miniGridStartY + row * (miniStackH + MINI_ROW_GAP);

            graphics.pose().pushPose();
            graphics.pose().translate(miniX, miniY, 0);
            graphics.pose().scale(MINI_SCALE, MINI_SCALE, 1f);
            drawStackRow(graphics, stacks.get(stackIdx), 0, 0, false, maxSmallSlots,
                elementForEntry(wandData, stackIdx), wandData.stacks().get(stackIdx));
            graphics.pose().popPose();
        }

        RenderSystem.disableBlend();
    }

    private static boolean isHoldingSpellWriter(Minecraft mc) {
        return mc.player.getMainHandItem().getItem() instanceof SpellWriter
            || mc.player.getOffhandItem().getItem() instanceof SpellWriter;
    }

    private static List<List<SpellComponent>> resolveStacks(WandStacksData wandData) {
        List<List<SpellComponent>> result = new ArrayList<>(wandData.stacks().size());
        for (WandStacksData.StackEntry entry : wandData.stacks()) {
            List<SpellComponent> resolved = new ArrayList<>(entry.components().size());
            for (WandStacksData.ComponentRef ref : entry.components()) {
                SpellComponent c = ref.kind() == WandStacksData.ComponentRef.KIND_TYPE
                    ? SpellTypeRegistry.get(ref.id())
                    : ModifierRegistry.get(ref.id());
                if (c != null) resolved.add(c);
            }
            result.add(resolved);
        }
        return result;
    }

    private static ResourceLocation elementForEntry(WandStacksData wandData, int idx) {
        if (idx < 0 || idx >= wandData.stacks().size()) return null;
        return wandData.stacks().get(idx).elementId();
    }

    private static boolean isModifierPermanent(WandStacksData.StackEntry entry, SpellModifier mod) {
        int modIdx = 0;
        for (int i = 0; i < entry.components().size(); i++) {
            WandStacksData.ComponentRef ref = entry.components().get(i);
            if (ref.kind() == WandStacksData.ComponentRef.KIND_MODIFIER) {
                if (ref.id().equals(mod.id())) {
                    return i < entry.permanentCount();
                }
                modIdx++;
            }
        }
        return false;
    }

    private static int fullRowWidth(int maxSmallSlots) {
        int cols = (int) Math.ceil(maxSmallSlots / (double) SMALL_ROWS);
        int smallAreaW = cols * SMALL_SIZE + Math.max(0, cols - 1) * SMALL_COL_GAP;
        return BIG_SIZE + BIG_SMALL_GAP + smallAreaW;
    }

    private static void drawStackRow(GuiGraphics g, List<SpellComponent> stack,
                                     int rowLeftX, int rowTopY, boolean isActive, int maxSmallSlots,
                                     ResourceLocation elementId, WandStacksData.StackEntry rawEntry) {
        SpellType typeInStack = null;
        List<SpellModifier> modifiers = new ArrayList<>();
        boolean hasCharges = false;
        int totalCasts = 0;
        for (SpellComponent c : stack) {
            if (typeInStack == null && c instanceof SpellType t) typeInStack = t;
            else if (c instanceof SpellModifier m) {
                modifiers.add(m);
                if (m instanceof ChargesModifier) hasCharges = true;
                if (m instanceof MultiCastModifier mcm) totalCasts = mcm.totalCasts();
            }
        }

        // Big slot background
        ResourceLocation bigSlotSprite;
        if (typeInStack != null) {
            ResourceLocation elemId = elementId != null ? elementId
                : (typeInStack.defaultElement() != null ? typeInStack.defaultElement().id() : null);
            String elemPath = elemId != null ? elemId.getPath() : "arcane";
            bigSlotSprite = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "hud/" + elemPath + "_overlay_slot_border");
        } else {
            bigSlotSprite = SLOT_BIG_EMPTY;
        }
        g.blitSprite(bigSlotSprite, rowLeftX, rowTopY, BIG_SIZE, BIG_SIZE);

        // Big icon
        if (typeInStack != null) {
            g.blit(typeInStack.getSpellIconPath(),
                rowLeftX + BIG_ICON_OFFSET,
                rowTopY + BIG_ICON_OFFSET,
                0, 0, BIG_ICON, BIG_ICON, BIG_ICON, BIG_ICON);
            if (rawEntry.inscribed() && rawEntry.hasPermanentSpellType()) {
                g.blit(LOCK_ICON, rowLeftX + BIG_SIZE - LOCK_SIZE - 1, rowTopY + 1,
                    LOCK_SIZE, LOCK_SIZE, 0, 0, 32, 32, 32, 32);
            }
        }

        // Selected border (only for active stack)
        if (isActive) {
            g.blitSprite(SLOT_SELECTED,
                rowLeftX + SELECTED_OFFSET,
                rowTopY + SELECTED_OFFSET,
                SELECTED_SIZE, SELECTED_SIZE);
        }

        // Small slot grid
        int smallStartX = rowLeftX + BIG_SIZE + BIG_SMALL_GAP;
        for (int i = 0; i < maxSmallSlots; i++) {
            int col = i / SMALL_ROWS;
            int rowWithinCol = i % SMALL_ROWS;
            int x = smallStartX + col * (SMALL_SIZE + SMALL_COL_GAP);
            int y = rowTopY + rowWithinCol * (SMALL_SIZE + SMALL_ROW_GAP);

            if (i < modifiers.size()) {
                SpellModifier mod = modifiers.get(i);
                g.blitSprite(mod.getOverlaySlotPath(), x, y, SMALL_SIZE, SMALL_SIZE);
                g.blit(mod.getSpellIconPath(), x, y, 0, 0, SMALL_ICON, SMALL_ICON, SMALL_ICON, SMALL_ICON);
                if (rawEntry.inscribed() && isModifierPermanent(rawEntry, mod)) {
                    int lockS = LOCK_SIZE - 2;
                    g.blit(LOCK_ICON, x + SMALL_SIZE - lockS, y, lockS, lockS,
                        0, 0, 32, 32, 32, 32);
                }
            } else {
                g.blitSprite(SLOT_SMALL_UNAVAIL, x, y, SMALL_SIZE, SMALL_SIZE);
            }
        }

        if (isActive && hasCharges && totalCasts > 0) {
            int remaining = ClientMagicData.charges();
            if (remaining <= 0 && typeInStack != null) remaining = totalCasts;
            if (remaining > 0) {
                int totalWidth = totalCasts * CHARGE_ICON_SIZE + (totalCasts - 1) * CHARGE_ICON_GAP;
                int chargeX = rowLeftX + (20 - totalWidth) / 2;
                int chargeY = rowTopY + BIG_SIZE - CHARGE_ICON_SIZE - 2;
                for (int i = 0; i < totalCasts; i++) {
                    int u = i < remaining ? 0 : CHARGE_ICON_SIZE;
                    g.blit(CHARGE_ICONS, chargeX, chargeY, u, 0,
                        CHARGE_ICON_SIZE, CHARGE_ICON_SIZE, CHARGE_TEX_SIZE, CHARGE_TEX_SIZE);
                    chargeX += CHARGE_ICON_SIZE + CHARGE_ICON_GAP;
                }
            }
        }
    }
}
