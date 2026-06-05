package com.github.runicrebirth.spells.modifiers;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import java.util.function.DoubleSupplier;
import net.minecraft.resources.ResourceLocation;

public class AdditiveSizeModifier implements SpellModifier {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "additive_size");

    private final DoubleSupplier deltaSource;

    public AdditiveSizeModifier(float fixedDelta) {
        this.deltaSource = () -> fixedDelta;
    }

    public AdditiveSizeModifier(DoubleSupplier deltaSource) {
        this.deltaSource = deltaSource;
    }

    @Override public ResourceLocation id() { return ID; }
    @Override public String iconName() { return "plus"; }

    @Override
    public void apply(SpellParams params) {
        params.size += (float) deltaSource.getAsDouble();
    }
}
