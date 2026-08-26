package com.github.runicrebirth.rune;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.api.item.IRunicDrone;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.items.SpellWriter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ElementRuneType extends RuneType {

    private static final List<String> STAT_KEYS = List.of("element_chance", "bonus_damage", "effect_radius");

    private final Element element;

    public ElementRuneType(Element element) {
        super(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_" + element.id().getPath()),
            element.id().getPath().substring(0, 1).toUpperCase() + element.id().getPath().substring(1)
        );
        this.element = element;
    }

    public Element element() { return element; }

    @Override public List<String> allStatKeys() { return STAT_KEYS; }

    @Override
    public float rollStat(String key, int tier, RandomSource rand) {
        return switch (key) {
            case "element_chance" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.01f, 0.25f);
                case 2 -> rangeRoll(rand, 0.25f, 0.50f);
                default -> rangeRoll(rand, 0.50f, 0.75f);
            };
            case "bonus_damage" -> switch (tier) {
                case 1 -> rangeRoll(rand, 0.00f, 0.15f);
                case 2 -> rangeRoll(rand, 0.15f, 0.30f);
                default -> rangeRoll(rand, 0.30f, 0.50f);
            };
            case "effect_radius" -> switch (tier) {
                case 1 -> rangeRoll(rand, 1.0f, 2.0f);
                case 2 -> rangeRoll(rand, 2.0f, 3.0f);
                default -> rangeRoll(rand, 3.0f, 5.0f);
            };
            default -> 0f;
        };
    }

    @Override
    public boolean applicableTo(ItemStack stack) {
        return stack.getItem() instanceof SpellWriter
            || stack.getItem() instanceof IMagicWeapon
            || stack.getItem() instanceof IRunicDrone;
    }

    @Override
    public void applyToParams(EnhancementRuneData data, SpellParams params) {
        params.element = element;
        float bonus = data.stats().getOrDefault("bonus_damage", 0f);
        if (bonus > 0) params.damage *= (1f + bonus);
    }
}
