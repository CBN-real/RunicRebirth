package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class DamageTypeBoostModifier implements SpellModifier {

    public static final Identifier ID_SHARP_BOOST = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "sharp_boost");
    public static final Identifier ID_BLUNT_BOOST = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "blunt_boost");
    public static final Identifier ID_MAGIC_BOOST = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "magic_boost");

    private final Identifier id;
    private final MagicDamageType targetType;
    private final float multiplier;

    public DamageTypeBoostModifier(Identifier id, MagicDamageType targetType, float multiplier) {
        this.id = id;
        this.targetType = targetType;
        this.multiplier = multiplier;
    }

    @Override
    public Identifier id() {
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
