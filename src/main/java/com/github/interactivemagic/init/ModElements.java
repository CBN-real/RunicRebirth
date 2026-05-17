package com.github.interactivemagic.init;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.spells.element.AirElement;
import com.github.interactivemagic.spells.element.ArcaneElement;
import com.github.interactivemagic.spells.element.EarthElement;
import com.github.interactivemagic.spells.element.FireElement;
import com.github.interactivemagic.spells.element.IceElement;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModElements {

    public static final DeferredHolder<Element, ArcaneElement> ARCANE =
        ElementRegistry.ELEMENTS.register("arcane", ArcaneElement::new);

    public static final DeferredHolder<Element, FireElement> FIRE =
        ElementRegistry.ELEMENTS.register("fire", FireElement::new);

    public static final DeferredHolder<Element, EarthElement> EARTH =
        ElementRegistry.ELEMENTS.register("earth", EarthElement::new);

    public static final DeferredHolder<Element, IceElement> ICE =
        ElementRegistry.ELEMENTS.register("ice", IceElement::new);

    public static final DeferredHolder<Element, AirElement> AIR =
        ElementRegistry.ELEMENTS.register("air", AirElement::new);

    private ModElements() {}

    public static void init() {}
}
