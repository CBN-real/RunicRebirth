package com.github.runicrebirth.spells.element;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class FireElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "fire");

    public int   igniteTicks          = 40;
    public float burstChance          = 0.50f;
    public float burstRadius          = 1.5f;
    public float burstDamagePercent   = 0.25f;

    @Override public ResourceLocation id() { return ID; }
    @Override public ParticleOptions particle(float scale) { return new ScaledParticleOption(ModParticles.FIRE_ELEMENT.get(), scale); }
    @Override public ParticleOptions tinyParticle(float scale) { return new ScaledParticleOption(ModParticles.FIRE_TINY.get(), scale); }
    @Override public ParticleOptions inkParticle(float scale) { return new ScaledParticleOption(ModParticles.FIRE_INK.get(), scale); }
    @Override public int displayColor() { return 0xFF6600; }

    @Override
    public void onHitEntity(float dealt, MagicDamageType damageType,
                            @Nullable LivingEntity caster,
                            LivingEntity target, ServerLevel level) {
        target.igniteForTicks(igniteTicks);

        if (level.random.nextFloat() < burstChance) {
            ParticleHelper.burstParticleEvent(level, particle(1.0f),
                target.position().add(0, 1, 0),
                28, burstRadius * 0.4, burstRadius * 0.5, burstRadius * 0.4, 0.08, 1.0f);

            float burstDamage = dealt * burstDamagePercent;
            AABB box = target.getBoundingBox().inflate(burstRadius);
            for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != target && e.distanceTo(target) <= burstRadius
                         && !DamageSources.isFriendlyFireBetween(caster, e))) {
                // null element prevents recursive onHitEntity trigger
                SpellDamageSource src = caster != null
                    ? SpellDamageSource.source(caster, damageType, null)
                    : SpellDamageSource.source(nearby, damageType, null);
                DamageSources.applyDamage(nearby, burstDamage, src);
            }
        }
    }
}
