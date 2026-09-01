package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicBindingEntity extends AbstractEffectSpellEntity {

    private static final int BIND_DURATION_TICKS = 100;

    private UUID casterUUID;
    private Vec3 bindPosition;
    private float totalDamage;

    public MagicBindingEntity(EntityType<? extends MagicBindingEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 0;
        this.damageCategory = MagicDamageType.SPIRIT;
        this.endTicks = 45;
    }

    public MagicBindingEntity(Level level, LivingEntity caster, LivingEntity target, SpellParams params) {
        this(ModEntities.MAGIC_BINDING.get(), level);
        this.casterUUID = caster.getUUID();
        this.bindPosition = target.position();
        this.totalDamage = params.damage;
        initFromParams(params);
        setFollowedEntity(target);
        this.setPos(target.getX(), target.getY(), target.getZ());
    }

    @Override
    protected void onActivated() {
        level().playSound(null, blockPosition(), ModSounds.SPELLS_INITIATE_BINDING.get(),
                SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    protected void beginEnding() {
        if (getPhase() != SpellPhase.ENDING) {
            level().playSound(null, blockPosition(), ModSounds.SPELLS_END_BINDING.get(),
                    SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        super.beginEnding();
    }

    @Override
    protected void onActiveTick() {
        if (!(this.level() instanceof ServerLevel server)) return;
        if (age > BIND_DURATION_TICKS) {
            burstParticles();
            beginEnding();
            return;
        }

        Entity bound = server.getEntity(followedUUID);
        if (bound == null || !bound.isAlive()) {
            beginEnding();
            return;
        }

        bound.teleportTo(bindPosition.x, bindPosition.y, bindPosition.z);
        bound.setDeltaMovement(Vec3.ZERO);
        bound.hurtMarked = true;
        this.setPos(bindPosition.x, bindPosition.y, bindPosition.z);

        if (age % 10 == 0 && bound instanceof LivingEntity living) {
            float tickDamage = totalDamage / (BIND_DURATION_TICKS / 3f);
            Entity caster = server.getEntity(casterUUID);
            if (caster instanceof LivingEntity casterLiving) {
                SpellDamageSource source = SpellDamageSource.source(this, casterLiving, damageCategory, element())
                    .withSpellType(com.github.runicrebirth.spells.types.MagicBinding.ID)
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
        double angle = this.level().getRandom().nextDouble() * Math.PI * 2;
        double dx = Math.cos(angle) * sz * 0.5;
        double dz = Math.sin(angle) * sz * 0.5;
        this.level().addParticle(element().particle(),
            pos.x + dx, pos.y + 0.5 + this.level().getRandom().nextDouble() * 1.5, pos.z + dz,
            0.0, 0.01, 0.0);
    }

    @Override
    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        ParticleHelper.burstParticleEvent(server, element().particle(), this.position().add(0, 1, 0),
            20, 0.5, 0.8, 0.5, 0.03, 1.0f);
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
