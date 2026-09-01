package com.github.runicrebirth.entities.spells;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class TargetCircleEntity extends Entity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float scale = 1.0f;

    public float getTargetScale() { return scale; }
    public void setTargetScale(float scale) { this.scale = scale; }

    public TargetCircleEntity(EntityType<? extends TargetCircleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TargetCircleEntity>("idle_ctrl", 0,
            state -> {
                state.setAnimation(IDLE);
                return PlayState.CONTINUE;
            }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) { return false; }
}
