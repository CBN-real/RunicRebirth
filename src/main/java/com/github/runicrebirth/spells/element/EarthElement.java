package com.github.runicrebirth.spells.element;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.entities.spells.EarthQuicksandEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class EarthElement implements Element {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "earth");

    public float quicksandChance        = 0.50f;
    public float quicksandRadius        = 2.0f;
    public int   quicksandDurationTicks = 60;

    @Override public Identifier id() { return ID; }
    @Override public ParticleOptions particle(float scale) { return new ScaledParticleOption(ModParticles.EARTH_ELEMENT.get(), scale); }
    @Override public ParticleOptions tinyParticle(float scale) { return new ScaledParticleOption(ModParticles.EARTH_TINY.get(), scale); }
    @Override public ParticleOptions inkParticle(float scale) { return new ScaledParticleOption(ModParticles.EARTH_INK.get(), scale); }
    @Override public int displayColor() { return 0x8B6914; }

    @Override
    public float bonusDamage(MagicDamageType damageType) {
        return damageType == MagicDamageType.BLUNT ? 3f : 0f;
    }

    @Override
    public void onHitEntity(float dealt, MagicDamageType damageType,
                            @Nullable LivingEntity caster,
                            LivingEntity target, ServerLevel level) {
        if (level.getRandom().nextFloat() < quicksandChance) {
            EarthQuicksandEntity quicksand = new EarthQuicksandEntity(
                ModEntities.EARTH_QUICKSAND.get(), level,
                target.getX(), target.getY(), target.getZ(),
                quicksandRadius, quicksandDurationTicks);
            level.addFreshEntity(quicksand);
        }
    }
}
