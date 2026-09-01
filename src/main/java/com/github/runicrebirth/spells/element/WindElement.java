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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WindElement implements Element {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "wind");

    public float burstChance          = 0.50f;
    public float burstRadius          = 1.5f;
    public float burstDamagePercent   = 0.25f;
    public float knockbackStrength    = 2.5f;

    @Override public Identifier id() { return ID; }
    @Override public ParticleOptions particle(float scale) { return new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), scale); }
    @Override public ParticleOptions tinyParticle(float scale) { return new ScaledParticleOption(ModParticles.WIND_TINY.get(), scale); }
    @Override public ParticleOptions inkParticle(float scale) { return new ScaledParticleOption(ModParticles.WIND_INK.get(), scale); }
    @Override public int displayColor() { return 0xCCFFCC; }

    @Override
    public float bonusDamage(MagicDamageType damageType) {
        return damageType == MagicDamageType.SHARP ? 3f : 0f;
    }

    @Override
    public void onHitEntity(float dealt, MagicDamageType damageType,
                            @Nullable LivingEntity caster,
                            LivingEntity target, ServerLevel level) {
        if (level.getRandom().nextFloat() < burstChance) {
            ParticleHelper.burstParticleEvent(level, particle(1.0f),
                target.position().add(0, 1, 0),
                28, burstRadius * 0.4, burstRadius * 0.5, burstRadius * 0.4, 0.12, 1.0f);

            float burstDamage = dealt * burstDamagePercent;
            AABB box = target.getBoundingBox().inflate(burstRadius);
            for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != target && e.distanceTo(target) <= burstRadius
                         && !DamageSources.isFriendlyFireBetween(caster, e))) {
                SpellDamageSource src = caster != null
                    ? SpellDamageSource.source(caster, damageType, null)
                    : SpellDamageSource.source(nearby, damageType, null);
                DamageSources.applyDamage(nearby, burstDamage, src);

                Vec3 awayDir = nearby.position().subtract(target.position());
                double dist = awayDir.length();
                if (dist > 0.01) {
                    Vec3 normalized = awayDir.scale(1.0 / dist);
                    nearby.knockback(knockbackStrength, -normalized.x, -normalized.z);
                }
            }
        }
    }
}
