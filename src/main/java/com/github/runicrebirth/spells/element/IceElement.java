package com.github.runicrebirth.spells.element;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class IceElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "ice");

    public float freezeChance         = 0.50f;
    public float bonusDamagePercent   = 0.25f;
    public int   freezeDurationTicks  = 40;

    @Override public ResourceLocation id() { return ID; }
    @Override public ParticleOptions particle(float scale) { return new ScaledParticleOption(ModParticles.ICE_ELEMENT.get(), scale); }
    @Override public ParticleOptions tinyParticle(float scale) { return new ScaledParticleOption(ModParticles.ICE_TINY.get(), scale); }
    @Override public ParticleOptions inkParticle(float scale) { return new ScaledParticleOption(ModParticles.ICE_INK.get(), scale); }
    @Override public int displayColor() { return 0x99CCFF; }

    @Override
    public void onHitEntity(float dealt, MagicDamageType damageType,
                            @Nullable LivingEntity caster,
                            LivingEntity target, ServerLevel level) {
        if (level.random.nextFloat() < freezeChance) {
            float bonus = dealt * bonusDamagePercent;
            SpellDamageSource bonusSrc = caster != null
                ? SpellDamageSource.source(caster, damageType, null)
                : SpellDamageSource.source(target, damageType, null);
            DamageSources.ignoreNextKnockback(target);
            DamageSources.applyDamage(target, bonus, bonusSrc);

            FrozenEffectEntity frozen = new FrozenEffectEntity(
                ModEntities.FROZEN_EFFECT.get(), level,
                target, bonusDamagePercent, freezeDurationTicks);
            level.addFreshEntity(frozen);
        }
    }
}
