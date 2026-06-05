package com.github.runicrebirth.entities.spells.demo;

import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.init.ModElements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpellDemoEntity extends Entity implements GeoEntity {

    private static final RawAnimation LOOP_INITIATE = RawAnimation.begin().thenLoop("initiate_spell");

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(SpellDemoEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long displayStartNanos;

    public SpellDemoEntity(EntityType<? extends SpellDemoEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
    }

    public String getElementId() {
        if (!this.isAddedToLevel()) {
            String override = BookDisplayState.getSelectedElement();
            if (override != null) return override;
        }
        return this.entityData.get(DATA_ELEMENT);
    }

    @Override
    public double getTick(Object entity) {
        if (!this.isAddedToLevel()) {
            long now = System.nanoTime();
            if (displayStartNanos == 0L) displayStartNanos = now;
            return (now - displayStartNanos) / 50_000_000.0;
        }
        return this.tickCount;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "demo", 0, state -> {
            state.setAnimation(LOOP_INITIATE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
