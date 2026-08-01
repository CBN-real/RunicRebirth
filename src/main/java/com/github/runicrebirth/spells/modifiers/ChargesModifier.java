package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class ChargesModifier implements SpellModifier {

    public static final ResourceLocation ID       = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "charges");
    public static final ResourceLocation ID_THREE = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "charges_three");
    public static final ResourceLocation ID_FOUR  = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "charges_four");

    private final ResourceLocation id;
    private final String iconName;
    private final int multiplier;

    public ChargesModifier(ResourceLocation id, String iconName, int multiplier) {
        this.id = id;
        this.iconName = iconName;
        this.multiplier = multiplier;
    }

    public int multiplier() { return multiplier; }

    @Override public ResourceLocation id() { return id; }
    @Override public String iconName() { return iconName; }

    @Override
    public String exclusivityGroup() {
        return "charges";
    }

    @Override
    public void apply(SpellParams params) {
        params.useCharges = true;
        params.chargesMultiplier = multiplier;
    }
}
