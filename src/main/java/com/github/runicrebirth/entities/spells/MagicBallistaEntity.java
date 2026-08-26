package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.util.ImpactHelper;
import com.github.runicrebirth.util.ParticleHelper;
import com.github.runicrebirth.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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

    private static final int CHARGE_TICKS = 57;
    private static final int PIN_TICKS = 30;
    private static final int MAX_LIFETIME_TICKS = 200;

    private Vec3 storedDirection;
    private float storedSpeed;
    private Entity pinnedEntity;
    private Vec3 pinPosition;


    public MagicBallistaEntity(EntityType<? extends MagicBallistaEntity> type, Level level) {
        super(type, level);
        this.damageCategory = MagicDamageType.SHARP;
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
        this.impact_radius = impact_radius * params.size;
    }

    @Override
    protected void onChargingTick() {
        if (phaseAge == 15) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_LOAD_BALLISTA.get(), SoundSource.PLAYERS, 2f, 1.0f);
        }
    }

    @Override
    protected void onActivated() {
        super.onActivated();
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
            ModSounds.SPELLS_SHOOT_BALLISTA.get(), SoundSource.PLAYERS, 2f, 0.9f);
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
        if (getPhase() != SpellPhase.ACTIVE) return;
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }

    private float impact_radius = 4.0f;

    private void applyImpact(ServerLevel server, Vec3 center, Entity directTarget) {
        Entity owner = this.getOwner();
        for (LivingEntity nearby : Utils.entitiesInRange(server, center, impact_radius, this)) {
            if (nearby == directTarget) continue;
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                    .withSpellType(com.github.runicrebirth.spells.types.MagicBallista.ID);
                DamageSources.applyDamage(nearby, damage, source);
            } else {
                nearby.hurt(this.damageSources().magic(), damage);
            }
        }
        ImpactHelper.createImpact(server, center, impact_radius, element(), 1.5f);
        ParticleHelper.burstParticleEvent(server, element().particle(), center,
            20, 0.4, 0.4, 0.4, 0.06, 1.0f);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                .withSpellType(com.github.runicrebirth.spells.types.MagicBallista.ID);
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
            applyImpact(server, hitLoc, target);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Vec3 hitLoc = result.getLocation();
        if (this.level() instanceof ServerLevel server) {
            applyImpact(server, hitLoc, null);
        }
        burstParticles();
        this.discard();
    }
}
