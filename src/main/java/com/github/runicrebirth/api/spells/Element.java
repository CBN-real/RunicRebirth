package com.github.runicrebirth.api.spells;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface Element {
    Identifier id();

    ParticleOptions particle(float scale);
    default ParticleOptions particle() { return particle(1.0f); }

    ParticleOptions tinyParticle(float scale);
    default ParticleOptions tinyParticle() { return tinyParticle(1.0f); }

    ParticleOptions inkParticle(float scale);
    default ParticleOptions inkParticle() { return inkParticle(1.0f); }

    int displayColor();

    default float bonusDamage(MagicDamageType damageType) { return 0f; }

    /** Called before a spell is cast to allow element to mutate {@link SpellParams}. */
    default void modifyParams(SpellParams params) {}

    /** Called server-side after spell damage is dealt to the target. */
    default void onHitEntity(float dealt, MagicDamageType damageType,
                             @Nullable LivingEntity caster,
                             LivingEntity target, ServerLevel level) {}

    /** @deprecated use {@link #onHitEntity} instead */
    @Deprecated
    default void applyStatusEffects(LivingEntity target) {}
}
