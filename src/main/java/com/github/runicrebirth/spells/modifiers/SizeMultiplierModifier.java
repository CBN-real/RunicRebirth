package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.ResourceLocation;

public class SizeMultiplierModifier implements SpellModifier {

    public static final ResourceLocation ID_PLUS = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus");
    public static final ResourceLocation ID_PLUS_TWO = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus_two");
    public static final ResourceLocation ID_PLUS_FOUR = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "size_plus_four");
    public static final String GROUP = "size";

    private final ResourceLocation id;
    private final String iconName;
    private final float multiplier;

    public SizeMultiplierModifier(ResourceLocation id, String iconName, float multiplier) {
        this.id = id;
        this.iconName = iconName;
        this.multiplier = multiplier;
    }

    @Override public ResourceLocation id() { return id; }
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
