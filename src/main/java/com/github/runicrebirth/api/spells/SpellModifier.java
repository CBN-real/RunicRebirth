package com.github.runicrebirth.api.spells;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public non-sealed interface SpellModifier extends SpellComponent {
    void apply(SpellParams params);

    default String exclusivityGroup() {
        return null;
    }

    default boolean canAppendTo(List<SpellComponent> existing) {
        return true;
    }

    @Override
    default ResourceLocation iconTexture() {
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/gui/spell/modifier/" + id().getPath() + ".png");
    }

    @Override
    default String iconSuffix() {
        return "_icon_small";
    }
}
