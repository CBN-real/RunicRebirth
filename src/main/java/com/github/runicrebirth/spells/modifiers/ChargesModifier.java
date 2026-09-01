package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class ChargesModifier implements SpellModifier {

    public static final Identifier ID       = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "charges");
    public static final Identifier ID_THREE = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "charges_three");
    public static final Identifier ID_FOUR  = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "charges_four");

    private final Identifier id;
    private final String iconName;
    private final int multiplier;

    public ChargesModifier(Identifier id, String iconName, int multiplier) {
        this.id = id;
        this.iconName = iconName;
        this.multiplier = multiplier;
    }

    public int multiplier() { return multiplier; }

    @Override public Identifier id() { return id; }
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
