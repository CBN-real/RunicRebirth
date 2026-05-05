package com.github.interactivemagic.api.spells;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

public interface Element {
    ResourceLocation id();

    ParticleOptions particle();

    int displayColor();
}
