package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicBindingEntity extends AbstractSpellEntity {

    private static final int BIND_DURATION_TICKS = 60;

    private UUID casterUUID;
    private UUID boundEntityUUID;
    private Vec3 bindPosition;
    private float totalDamage;

    public MagicBindingEntity(EntityType<? extends MagicBindingEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 0;
        this.damageCategory = MagicDamageType.SPIRIT;
    }

    public MagicBindingEntity(Level level, LivingEntity caster, LivingEntity target, SpellParams params) {
        this(ModEntities.MAGIC_BINDING.get(), level);
        this.casterUUID = caster.getUUID();
        this.boundEntityUUID = target.getUUID();
        this.bindPosition = target.position();
        this.totalDamage = params.damage;
        initFromParams(params);
        this.setPos(target.getX(), target.getY(), target.getZ());
    }

    @Override
    protected void onActiveTick() {
        if (!(this.level() instanceof ServerLevel server)) return;
        if (age > BIND_DURATION_TICKS) {
            burstParticles();
            beginEnding();
            return;
        }

        Entity bound = server.getEntity(boundEntityUUID);
        if (bound == null || !bound.isAlive()) {
            beginEnding();
            return;
        }

        bound.teleportTo(bindPosition.x, bindPosition.y, bindPosition.z);
        bound.setDeltaMovement(Vec3.ZERO);
        bound.hurtMarked = true;
        this.setPos(bindPosition.x, bindPosition.y, bindPosition.z);

        if (age % 3 == 0 && bound instanceof LivingEntity living) {
            float tickDamage = totalDamage / (BIND_DURATION_TICKS / 3f);
            Entity caster = server.getEntity(casterUUID);
            if (caster instanceof LivingEntity casterLiving) {
                SpellDamageSource source = SpellDamageSource.source(this, casterLiving, damageCategory, element())
                    .setIFrames(0);
                DamageSources.ignoreNextKnockback(living);
                DamageSources.applyDamage(living, tickDamage, source);
            } else {
                DamageSources.ignoreNextKnockback(living);
                living.hurt(this.damageSources().magic(), tickDamage);
            }
        }
    }

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        float sz = getProjectileSize();
        double angle = this.level().random.nextDouble() * Math.PI * 2;
        double dx = Math.cos(angle) * sz * 0.5;
        double dz = Math.sin(angle) * sz * 0.5;
        this.level().addParticle(ModParticles.FIRE_ELEMENT.get(),
            pos.x + dx, pos.y + 0.5 + this.level().random.nextDouble() * 1.5, pos.z + dz,
            0.0, 0.01, 0.0);
    }

    @Override
    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        server.sendParticles(element().particle(), pos.x, pos.y + 1, pos.z, 20, 0.5, 0.8, 0.5, 0.03);
    }
}
