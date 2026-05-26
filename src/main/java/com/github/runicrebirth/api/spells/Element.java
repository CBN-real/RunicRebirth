package com.github.runicrebirth.api.spells;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface Element {
    ResourceLocation id();

    ParticleOptions particle(float scale);
    default ParticleOptions particle() { return particle(1.0f); }

    ParticleOptions tinyParticle(float scale);
    default ParticleOptions tinyParticle() { return tinyParticle(1.0f); }

    ParticleOptions inkParticle(float scale);
    default ParticleOptions inkParticle() { return inkParticle(1.0f); }

    int displayColor();

    default float bonusDamage(MagicDamageType damageType) { return 0f; }

    default void applyStatusEffects(LivingEntity target) {}
}
