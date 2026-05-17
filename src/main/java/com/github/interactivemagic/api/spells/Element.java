package com.github.interactivemagic.api.spells;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface Element {
    ResourceLocation id();

    ParticleOptions particle();

    int displayColor();

    default float bonusDamage(MagicDamageType damageType) { return 0f; }

    default void applyStatusEffects(LivingEntity target) {}
}
