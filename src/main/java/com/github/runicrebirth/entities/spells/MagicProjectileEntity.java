package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicProjectileEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 80;

    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, Level level) {
        super(type, level);
        this.endTicks = 20;
        this.chargeTicks = 7;
    }

    public MagicProjectileEntity(Level level, LivingEntity owner, SpellParams params, Vec3 direction) {
        super(ModEntities.MAGIC_PROJECTILE.get(), owner, level, direction, params.speed);
        initFromParams(params);
        this.endTicks = 20;
        this.chargeTicks = 7;
    }

    @Override
    protected void onActivated() {
        super.onActivated();
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
            ModSounds.SPELLS_PROJECTILE_SHOOT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    protected void onActiveTick() {
        if (age > MAX_LIFETIME_TICKS) {
            beginEnding();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                .withSpellType(com.github.runicrebirth.spells.types.MagicProjectile.ID);
            DamageSources.applyDamage(target, damage, source);
        } else {
            target.hurt(this.damageSources().magic(), damage);
        }
        burstParticles();
        beginEnding();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        burstParticles();
        beginEnding();
    }
}
