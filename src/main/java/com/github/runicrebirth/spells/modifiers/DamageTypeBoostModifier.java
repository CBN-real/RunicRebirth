package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class DamageTypeBoostModifier implements SpellModifier {

    public static final ResourceLocation ID_SHARP_BOOST = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "sharp_boost");
    public static final ResourceLocation ID_BLUNT_BOOST = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "blunt_boost");
    public static final ResourceLocation ID_MAGIC_BOOST = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_boost");

    private final ResourceLocation id;
    private final MagicDamageType targetType;
    private final float multiplier;

    public DamageTypeBoostModifier(ResourceLocation id, MagicDamageType targetType, float multiplier) {
        this.id = id;
        this.targetType = targetType;
        this.multiplier = multiplier;
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public void apply(SpellParams params) {
        if (params.damageCategory == targetType) {
            params.damage *= multiplier;
        }
    }

    @Override
    public String exclusivityGroup() {
        return "damage_boost";
    }
}
