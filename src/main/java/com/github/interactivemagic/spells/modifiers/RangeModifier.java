package com.github.interactivemagic.spells.modifiers;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class RangeModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "range");

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "range"; }

    @Override
    public void apply(SpellParams params) {
        params.speed *= 1.5f;
        params.rangeMultiplier = 1.5f;
    }
}
