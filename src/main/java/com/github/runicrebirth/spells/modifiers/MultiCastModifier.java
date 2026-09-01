package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;

public class MultiCastModifier implements SpellModifier {

    public static final Identifier ID_TWO = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "two_casts");
    public static final Identifier ID_FOUR = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "four_casts");
    public static final String GROUP = "casts";

    private final Identifier id;
    private final String iconName;
    private final int totalCasts;

    public MultiCastModifier(Identifier id, String iconName, int totalCasts) {
        this.id = id;
        this.iconName = iconName;
        this.totalCasts = totalCasts;
    }

    @Override public Identifier id() { return id; }
    @Override public String iconName() { return iconName; }
    public int totalCasts() { return totalCasts; }

    @Override
    public String exclusivityGroup() {
        return GROUP;
    }

    @Override
    public void apply(SpellParams params) {
        params.extraCasts = totalCasts - 1;
    }
}
