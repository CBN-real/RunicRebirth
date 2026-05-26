package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class MagicBallistaEntity extends AbstractProjectileSpellEntity {

    private static final int CHARGE_TICKS = 84;
    private static final int PIN_TICKS = 30;
    private static final int MAX_LIFETIME_TICKS = 200;

    private Vec3 storedDirection;
    private float storedSpeed;
    private Entity pinnedEntity;
    private Vec3 pinPosition;

    public MagicBallistaEntity(EntityType<? extends MagicBallistaEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = CHARGE_TICKS;
        this.endTicks = PIN_TICKS;
    }

    public MagicBallistaEntity(Level level, LivingEntity owner, SpellParams params,
                               Vec3 direction) {
        super(ModEntities.MAGIC_BALLISTA.get(), owner, level, direction, params.speed);
        initFromParams(params);
        this.damageCategory = MagicDamageType.SHARP;
        this.chargeTicks = CHARGE_TICKS;
        this.endTicks = PIN_TICKS;
    }

    @Override
    protected void onActiveTick() {
        if (age > MAX_LIFETIME_TICKS) {
            beginEnding();
        }
    }

    @Override
    protected void onEndingTick() {
        if (pinPosition != null) {
            this.setPos(pinPosition.x, pinPosition.y, pinPosition.z);
        }
        if (pinnedEntity != null && pinnedEntity.isAlive()) {
            pinnedEntity.teleportTo(pinPosition.x, pinPosition.y, pinPosition.z);
            pinnedEntity.setDeltaMovement(Vec3.ZERO);
            pinnedEntity.hurtMarked = true;
        }
        if (phaseAge >= endTicks) {
            burstParticles();
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

        pinnedEntity = target;
        Vec3 hitLoc = result.getLocation();
        Vec3 dir = this.getDeltaMovement().normalize();
        pinPosition = hitLoc.add(dir.scale(target.getBbWidth() * 0.5));
        beginEnding();
        this.setPos(pinPosition.x, pinPosition.y, pinPosition.z);

        if (this.level() instanceof ServerLevel server) {
            ParticleHelper.burstParticleEvent(server, element().particle(), hitLoc,
                10, 0.2, 0.2, 0.2, 0.03, 1.0f);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        burstParticles();
        this.discard();
    }
}
