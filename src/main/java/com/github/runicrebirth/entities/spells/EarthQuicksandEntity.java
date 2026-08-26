package com.github.runicrebirth.entities.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;

public class EarthQuicksandEntity extends net.minecraft.world.entity.Entity {

    public float quicksandRadius  = 2.0f;
    public int   durationTicks    = 60;

    private static final int SLOWNESS_AMPLIFIER = 1;
    private static final int SLOWNESS_REFRESH_TICKS = 5;

    public EarthQuicksandEntity(EntityType<? extends EarthQuicksandEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public EarthQuicksandEntity(EntityType<? extends EarthQuicksandEntity> type, Level level,
                                double x, double y, double z,
                                float radius, int duration) {
        this(type, level);
        this.quicksandRadius = radius;
        this.durationTicks = duration;
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnDustParticles();
            return;
        }

        if (tickCount > durationTicks) {
            discard();
            return;
        }

        AABB zone = new AABB(
            getX() - quicksandRadius, getY() - 0.5, getZ() - quicksandRadius,
            getX() + quicksandRadius, getY() + 3.0, getZ() + quicksandRadius);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, zone,
                en -> en.distanceTo(this) <= quicksandRadius)) {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                SLOWNESS_REFRESH_TICKS, SLOWNESS_AMPLIFIER, false, false));
        }
    }

    private void spawnDustParticles() {
        if (level().random.nextInt(2) != 0) return;
        double angle = level().random.nextDouble() * Math.PI * 2;
        double dist  = level().random.nextDouble() * quicksandRadius;
        level().addParticle(
            new DustParticleOptions(new Vector3f(0.55f, 0.42f, 0.11f), 1.2f),
            getX() + Math.cos(angle) * dist,
            getY() + 0.05,
            getZ() + Math.sin(angle) * dist,
            0, 0.04, 0);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean isPickable() { return false; }
}
