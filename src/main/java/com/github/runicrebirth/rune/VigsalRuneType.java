package com.github.runicrebirth.rune;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VigsalRuneType extends RuneType {

    private static final List<String> STAT_KEYS = List.of("aura_damage", "durability_increase", "active_cooldown_reduction");

    public VigsalRuneType() {
        super(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), "Vígsál");
    }

    @Override public List<String> allStatKeys() { return STAT_KEYS; }

    @Override
    public float rollStat(String key, int tier, RandomSource rand) {
        return switch (key) {
            case "aura_damage" -> switch (tier) {
                case 1 -> rangeRoll(rand, 1f, 2f);
                case 2 -> rangeRoll(rand, 2f, 4f);
                default -> rangeRoll(rand, 4f, 8f);
            };
            case "durability_increase" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.25f);
                case 2 -> rangeRoll(rand, 0.25f, 0.75f);
                default -> rangeRoll(rand, 0.75f, 1.50f);
            };
            case "active_cooldown_reduction" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.10f);
                case 2 -> rangeRoll(rand, 0.10f, 0.25f);
                default -> rangeRoll(rand, 0.25f, 0.50f);
            };
            default -> 0f;
        };
    }

    @Override
    public boolean applicableTo(ItemStack stack) {
        return stack.getItem() instanceof IMagicWeapon;
    }

    @Override
    public void applyToParams(EnhancementRuneData data, SpellParams params) {
        // Vígsál effects applied in melee/activate pipeline, not SpellParams
    }
}
