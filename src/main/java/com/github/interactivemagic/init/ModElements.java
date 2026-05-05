package com.github.interactivemagic.init;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.spells.element.ArcaneElement;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModElements {

    public static final DeferredHolder<Element, ArcaneElement> ARCANE =
        ElementRegistry.ELEMENTS.register("arcane", ArcaneElement::new);

    private ModElements() {}

    public static void init() {}
}
