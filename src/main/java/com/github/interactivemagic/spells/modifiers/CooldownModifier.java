package com.github.interactivemagic.spells.modifiers;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class CooldownModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "cooldown");

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "cooldown"; }

    @Override
    public void apply(SpellParams params) {
        params.castingDelayTicks = Math.max(1, params.castingDelayTicks / 2);
    }
}
