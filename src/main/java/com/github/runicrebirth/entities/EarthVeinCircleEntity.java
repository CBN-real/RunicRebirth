package com.github.runicrebirth.entities;

import com.github.runicrebirth.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EarthVeinCircleEntity extends Entity implements GeoEntity {

    private static final RawAnimation ANIM_INITIATE = RawAnimation.begin().thenPlay("initiate");
    private static final RawAnimation ANIM_HOLD = RawAnimation.begin().thenLoop("hold");
    private static final RawAnimation ANIM_END = RawAnimation.begin().thenPlay("end");
    private static final RawAnimation ANIM_UNLOCK = RawAnimation.begin().thenPlay("unlock");

    public enum AnimState { INITIATING, HOLDING, ENDING, UNLOCKING }

    private static final EntityDataAccessor<Integer> ANIM_STATE =
            SynchedEntityData.defineId(EarthVeinCircleEntity.class, EntityDataSerializers.INT);

    private int animTimer = 0;

    private static final int INITIATE_TICKS = 40;
    private static final int END_TICKS = 40;
    private static final int UNLOCK_TICKS = 160;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EarthVeinCircleEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        animTimer++;
        AnimState state = getAnimState();
        if (state == AnimState.INITIATING && animTimer >= INITIATE_TICKS) {
            setAnimState(AnimState.HOLDING);
            animTimer = 0;
        } else if (state == AnimState.ENDING && animTimer >= END_TICKS) {
            this.discard();
        } else if (state == AnimState.UNLOCKING && animTimer >= UNLOCK_TICKS) {
            setAnimState(AnimState.HOLDING);
            animTimer = 0;
        }
    }

    public void triggerEnd() {
        setAnimState(AnimState.ENDING);
        animTimer = 0;
    }

    public void triggerUnlockAnim() {
        setAnimState(AnimState.UNLOCKING);
        animTimer = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "ctrl", 5, state -> {
            return switch (getAnimState()) {
                case INITIATING -> state.setAndContinue(ANIM_INITIATE);
                case HOLDING -> state.setAndContinue(ANIM_HOLD);
                case ENDING -> state.setAndContinue(ANIM_END);
                case UNLOCKING -> state.setAndContinue(ANIM_UNLOCK);
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return new AABB(getX() - 3.5, getY() - 0.5, getZ() - 3.5, getX() + 3.5, getY() + 0.5, getZ() + 3.5);
    }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean shouldBeSaved() { return false; }

    private AnimState getAnimState() {
        return AnimState.values()[entityData.get(ANIM_STATE)];
    }

    private void setAnimState(AnimState state) {
        entityData.set(ANIM_STATE, state.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ANIM_STATE, AnimState.INITIATING.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
