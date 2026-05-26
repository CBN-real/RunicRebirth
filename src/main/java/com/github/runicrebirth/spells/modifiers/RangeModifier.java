package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class RangeModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "range");

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "range"; }

    @Override
    public void apply(SpellParams params) {
        params.speed *= 1.5f;
        params.rangeMultiplier = 1.5f;
    }
}
