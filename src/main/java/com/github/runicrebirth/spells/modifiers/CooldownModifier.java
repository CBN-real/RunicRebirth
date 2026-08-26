package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class CooldownModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "cooldown");

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "cooldown"; }

    @Override
    public void apply(SpellParams params) {
        params.castingDelayTicks = (int) Math.max(1, params.castingDelayTicks * 0.75f);
    }
}
