package com.github.runicrebirth.entities.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

        if (level().isClientSide()) {
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
            e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                SLOWNESS_REFRESH_TICKS, SLOWNESS_AMPLIFIER, false, false));
        }
    }

    private void spawnDustParticles() {
        if (level().getRandom().nextInt(2) != 0) return;
        double angle = level().getRandom().nextDouble() * Math.PI * 2;
        double dist  = level().getRandom().nextDouble() * quicksandRadius;
        level().addParticle(
            new DustParticleOptions(0xFF8C6B1C, 1.2f),
            getX() + Math.cos(angle) * dist,
            getY() + 0.05,
            getZ() + Math.sin(angle) * dist,
            0, 0.04, 0);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}
    @Override public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) { return false; }
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean isPickable() { return false; }

}
