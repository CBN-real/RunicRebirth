package com.github.runicrebirth.rune;

import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RuneEffectApplicator {

    private RuneEffectApplicator() {}

    public static void applyRuneEffectsToParams(ItemStack stack, SpellParams params) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null || runes.isEmpty()) return;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type != null) type.applyToParams(rune, params);
        }
    }

    public static Element getActiveElement(ItemStack stack) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return null;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof ElementRuneType ert) return ert.element();
        }
        return null;
    }

    public static float getVigsalAuraBonus(ItemStack stack) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return 0f;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof VigsalRuneType) {
                return rune.stats().getOrDefault("aura_damage", 0f);
            }
        }
        return 0f;
    }

    public static float getVigsalActiveCooldownFactor(ItemStack stack) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return 1f;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof VigsalRuneType) {
                float reduction = rune.stats().getOrDefault("active_cooldown_reduction", 0f);
                return 1f - reduction;
            }
        }
        return 1f;
    }

    public static float getVigsalDurabilityBonus(ItemStack stack, int baseDurability) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return 0f;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof VigsalRuneType) {
                float pct = rune.stats().getOrDefault("durability_increase", 0f);
                return baseDurability * pct;
            }
        }
        return 0f;
    }

    public static float getYotorDamageBonus(ItemStack stack) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return 0f;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof YotorRuneType) {
                return rune.stats().getOrDefault("active_damage", 0f);
            }
        }
        return 0f;
    }

    public static float getYotorCooldownFactor(ItemStack stack) {
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null) return 1f;
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type instanceof YotorRuneType) {
                float reduction = rune.stats().getOrDefault("active_cooldown_reduction", 0f);
                return 1f - reduction;
            }
        }
        return 1f;
    }
}
