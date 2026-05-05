package com.github.interactivemagic.spells.element;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ArcaneElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "arcane");

    @Override public ResourceLocation id() { return ID; }

  @Override public ParticleOptions particle() { return ParticleHelper.ARCANE; }
    @Override public int displayColor() { return 0xAE78FF; }
}
