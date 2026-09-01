package com.github.runicrebirth.rune;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IRunicDrone;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class YotorRuneType extends RuneType {

    private static final List<String> STAT_KEYS = List.of("active_damage", "active_cooldown_reduction");

    public YotorRuneType() {
        super(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), "Yotor");
    }

    @Override public List<String> allStatKeys() { return STAT_KEYS; }

    @Override
    public float rollStat(String key, int tier, RandomSource rand) {
        return switch (key) {
            case "active_damage" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.25f);
                case 2 -> rangeRoll(rand, 0.25f, 0.50f);
                default -> rangeRoll(rand, 0.50f, 1.00f);
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
        return stack.getItem() instanceof IRunicDrone;
    }

    @Override
    public void applyToParams(EnhancementRuneData data, SpellParams params) {
        // Yotor effects applied in drone pipeline, not SpellParams
    }
}
