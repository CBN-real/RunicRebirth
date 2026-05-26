package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class MultiCastModifier implements SpellModifier {

    public static final ResourceLocation ID_TWO = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "two_casts");
    public static final ResourceLocation ID_FOUR = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "four_casts");
    public static final String GROUP = "casts";

    private final ResourceLocation id;
    private final String iconName;
    private final int totalCasts;

    public MultiCastModifier(ResourceLocation id, String iconName, int totalCasts) {
        this.id = id;
        this.iconName = iconName;
        this.totalCasts = totalCasts;
    }

    @Override public ResourceLocation id() { return id; }
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
