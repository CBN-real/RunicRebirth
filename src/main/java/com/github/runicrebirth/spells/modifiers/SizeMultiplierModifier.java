package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class SizeMultiplierModifier implements SpellModifier {

    public static final Identifier ID_PLUS = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus");
    public static final Identifier ID_PLUS_TWO = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus_two");
    public static final Identifier ID_PLUS_FOUR = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus_four");
    public static final String GROUP = "size";

    private final Identifier id;
    private final String iconName;
    private final float multiplier;

    public SizeMultiplierModifier(Identifier id, String iconName, float multiplier) {
        this.id = id;
        this.iconName = iconName;
        this.multiplier = multiplier;
    }

    @Override public Identifier id() { return id; }
    @Override public String iconName() { return iconName; }

    @Override
    public String exclusivityGroup() {
        return GROUP;
    }

    @Override
    public void apply(SpellParams params) {
        params.size *= multiplier;
    }
}
