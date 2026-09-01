package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class RangeModifier implements SpellModifier {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "range");

    @Override public Identifier id() { return ID; }
    @Override public String iconName() { return "range"; }

    @Override
    public void apply(SpellParams params) {
        params.speed *= 1.5f;
        params.rangeMultiplier = 1.5f;
    }
}
