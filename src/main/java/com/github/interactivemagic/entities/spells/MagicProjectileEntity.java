package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicProjectileEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 80;
    private Vec3 storedDirection;
    private float storedSpeed;

    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MagicProjectileEntity(Level level, LivingEntity owner, SpellParams params, Vec3 direction) {
        super(ModEntities.MAGIC_PROJECTILE.get(), owner, level);
        initFromParams(params);
        this.storedDirection = direction;
        this.storedSpeed = params.speed;
    }

    @Override
    protected void onActiveTick() {
        if (age > MAX_LIFETIME_TICKS) {
            beginEnding();
        }
    }

    @Override
    protected void onActivated() {
      if (storedDirection != null) {
        this.shoot(storedDirection.x, storedDirection.y, storedDirection.z, this.storedSpeed, 0.0F);
      }
    }

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
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
