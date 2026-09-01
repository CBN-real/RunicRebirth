package com.github.runicrebirth.rune;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicStaff;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArcanumRuneType extends RuneType {

    private static final List<String> STAT_KEYS = List.of("size_modifier", "extra_casts", "spell_damage");

    public ArcanumRuneType() {
        super(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), "Arcanum");
    }

    @Override public List<String> allStatKeys() { return STAT_KEYS; }

    @Override
    public float rollStat(String key, int tier, RandomSource rand) {
        return switch (key) {
            case "size_modifier" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.25f);
                case 2 -> rangeRoll(rand, 0.25f, 0.50f);
                default -> rangeRoll(rand, 0.50f, 1.00f);
            };
            case "extra_casts" -> tier;
            case "spell_damage" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.25f);
                case 2 -> rangeRoll(rand, 0.25f, 0.50f);
                default -> rangeRoll(rand, 0.50f, 1.00f);
            };
            default -> 0f;
        };
    }

    @Override
    public boolean applicableTo(ItemStack stack) {
        return stack.getItem() instanceof IMagicStaff;
    }

    @Override
    public void applyToParams(EnhancementRuneData data, SpellParams params) {
        float sizeBonus = data.stats().getOrDefault("size_modifier", 0f);
        if (sizeBonus > 0) params.size *= (1f + sizeBonus);
        float extraCasts = data.stats().getOrDefault("extra_casts", 0f);
        if (extraCasts > 0) params.extraCasts += (int) extraCasts;
        float dmgBonus = data.stats().getOrDefault("spell_damage", 0f);
        if (dmgBonus > 0) params.damage *= (1f + dmgBonus);
    }
}
