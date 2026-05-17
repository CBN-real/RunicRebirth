package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicArrowEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 200;
    private Vec3 storedDirection;
    private float storedSpeed;

    public MagicArrowEntity(EntityType<? extends MagicArrowEntity> type, Level level) {
        super(type, level);
        this.damageCategory = MagicDamageType.SHARP;
    }

    public MagicArrowEntity(Level level, LivingEntity owner, SpellParams params, Vec3 direction,
        float xRot, float yRot) {
        super(ModEntities.MAGIC_ARROW.get(), owner, level);
        initFromParams(params);
        this.storedDirection = direction;
        this.storedSpeed = params.speed;
        this.shoot(storedDirection.x, storedDirection.y, storedDirection.z, 0.001F, 0.0F);
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
        var vec = getDeltaMovement();
        var length = vec.length();
        int count = (int) Math.min(20, Math.round(length) * 2) + 1;
        float f = (float) length / count;
        for (int i = 0; i < count; i++) {
          Vec3 rand = Utils.randVec3(0.025);
          Vec3 particleVec = vec.scale(f * i);
          this.level().addParticle(element().particle(), this.getX() + rand.x + particleVec.x,
              this.getY() + rand.y + particleVec.y,
              this.getZ() + rand.z + particleVec.z, rand.x, rand.y, rand.z);
        }
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
