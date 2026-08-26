package com.github.runicrebirth.rune;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWand;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OrderRuneType extends RuneType {

    private static final List<String> STAT_KEYS = List.of("cooldown_reduction", "extra_charges", "spell_damage");

    public OrderRuneType() {
        super(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), "Order");
    }

    @Override public List<String> allStatKeys() { return STAT_KEYS; }

    @Override
    public float rollStat(String key, int tier, RandomSource rand) {
        return switch (key) {
            case "cooldown_reduction" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.10f);
                case 2 -> rangeRoll(rand, 0.10f, 0.25f);
                default -> rangeRoll(rand, 0.25f, 0.50f);
            };
            case "extra_charges" -> switch (tier) {
                case 1 -> rangeRoll(rand, 1f, 2f);
                case 2 -> rangeRoll(rand, 2f, 4f);
                default -> rangeRoll(rand, 4f, 8f);
            };
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
        return stack.getItem() instanceof IMagicWand;
    }

    @Override
    public void applyToParams(EnhancementRuneData data, SpellParams params) {
        float cooldownReduce = data.stats().getOrDefault("cooldown_reduction", 0f);
        if (cooldownReduce > 0) params.cooldownReductionFactor *= (1f - cooldownReduce);
        float extraCharges = data.stats().getOrDefault("extra_charges", 0f);
        if (extraCharges > 0) params.chargesBonus += (int) extraCharges;
        float dmgBonus = data.stats().getOrDefault("spell_damage", 0f);
        if (dmgBonus > 0) params.damage *= (1f + dmgBonus);
    }
}
