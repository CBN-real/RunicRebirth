package com.github.runicrebirth.api.spells;

import java.util.List;
import net.minecraft.resources.Identifier;

public non-sealed interface SpellModifier extends SpellComponent {
    void apply(SpellParams params);

    default String exclusivityGroup() {
        return null;
    }

    default boolean canAppendTo(List<SpellComponent> existing) {
        return true;
    }

    @Override
    default Identifier iconTexture() {
        return Identifier.fromNamespaceAndPath(id().getNamespace(), "textures/gui/spell/modifier/" + id().getPath() + ".png");
    }

    @Override
    default String iconSuffix() {
        return "_icon_small";
    }
}
