package com.github.interactivemagic.spells.modifiers;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.api.spells.SpellParams;
import java.util.function.IntSupplier;
import net.minecraft.resources.ResourceLocation;

public class AdditiveSizeModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "additive_size");

    private final IntSupplier deltaSource;

    public AdditiveSizeModifier(int fixedDelta) {
        this.deltaSource = () -> fixedDelta;
    }

    public AdditiveSizeModifier(IntSupplier deltaSource) {
        this.deltaSource = deltaSource;
    }

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "plus"; }

    @Override
    public void apply(SpellParams params) {
        params.size += deltaSource.getAsInt();
    }
}
