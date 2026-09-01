package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class CooldownModifier implements SpellModifier {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "cooldown");

    @Override public Identifier id() { return ID; }
    @Override public String iconName() { return "cooldown"; }

    @Override
    public void apply(SpellParams params) {
        params.castingDelayTicks = (int) Math.max(1, params.castingDelayTicks * 0.75f);
    }
}
