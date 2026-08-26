package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class FrozenEffectEntity extends Entity implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_TARGET_ID =
        SynchedEntityData.defineId(FrozenEffectEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int   freezeDurationTicks = 40;
    public float bonusDamagePercent  = 0.25f;

    private UUID targetUUID;

    public FrozenEffectEntity(EntityType<? extends FrozenEffectEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public FrozenEffectEntity(EntityType<? extends FrozenEffectEntity> type, Level level,
                              LivingEntity target, float bonusDamagePercent, int freezeDurationTicks) {
        this(type, level);
        this.targetUUID = target.getUUID();
        this.bonusDamagePercent = bonusDamagePercent;
        this.freezeDurationTicks = freezeDurationTicks;
        this.entityData.set(DATA_TARGET_ID, target.getId());
        this.setPos(target.getX(), target.getY(), target.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGET_ID, -1);
    }

    public int getTargetEntityId() { return this.entityData.get(DATA_TARGET_ID); }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnTrickleParticles();
            return;
        }
        if (!(level() instanceof ServerLevel server)) return;

        Entity target = server.getEntity(targetUUID);
        if (target == null || !target.isAlive()) {
            discard();
            return;
        }

        if (tickCount > freezeDurationTicks) {
            burstParticles(server, target.getBbWidth());
            discard();
            return;
        }

        setPos(target.getX(), target.getY(), target.getZ());
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;
    }

    private void spawnTrickleParticles() {
        Entity target = level().getEntity(getTargetEntityId());
        float width = target != null ? target.getBbWidth() : 1.0f;
        if (level().random.nextInt(2) == 0) {
            level().addParticle(
                new ScaledParticleOption(ModParticles.ICE_ELEMENT.get(), 0.5f * width),
                getX() + level().random.nextGaussian() * 0.3 * width,
                getY() + level().random.nextDouble() * 2.0,
                getZ() + level().random.nextGaussian() * 0.3 * width,
                0, 0.02, 0);
        }
    }

    private void burstParticles(ServerLevel server, float width) {
        ParticleHelper.burstParticleEvent(server,
            new ScaledParticleOption(ModParticles.ICE_ELEMENT.get(), 1.0f * width),
            position().add(0, 1, 0), 35, 0.5 * width, 0.8, 0.5 * width, 0.06, 1.0f);
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean isPickable() { return false; }
}
