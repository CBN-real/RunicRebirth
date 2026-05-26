package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class ChargesModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "charges");

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "charges"; }

    @Override
    public String exclusivityGroup() {
        return "charges";
    }

    @Override
    public boolean canAppendTo(List<SpellComponent> existing) {
        for (SpellComponent c : existing) {
            if (c instanceof SpellModifier m && MultiCastModifier.GROUP.equals(m.exclusivityGroup())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(SpellParams params) {
        params.useCharges = true;
    }
}
