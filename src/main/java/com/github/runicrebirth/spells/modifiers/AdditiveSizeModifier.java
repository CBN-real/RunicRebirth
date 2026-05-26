package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import java.util.function.IntSupplier;
import net.minecraft.resources.ResourceLocation;

public class AdditiveSizeModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "additive_size");

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
