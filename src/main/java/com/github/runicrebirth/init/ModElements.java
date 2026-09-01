package com.github.runicrebirth.init;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.spells.element.WindElement;
import com.github.runicrebirth.spells.element.ArcaneElement;
import com.github.runicrebirth.spells.element.EarthElement;
import com.github.runicrebirth.spells.element.FireElement;
import com.github.runicrebirth.spells.element.IceElement;
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

    public static final DeferredHolder<Element, WindElement> WIND =
        ElementRegistry.ELEMENTS.register("wind", WindElement::new);

    private ModElements() {}

    public static void init() {}
}
