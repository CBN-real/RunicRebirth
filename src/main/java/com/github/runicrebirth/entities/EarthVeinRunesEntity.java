package com.github.runicrebirth.entities;

import com.github.runicrebirth.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class EarthVeinRunesEntity extends Entity implements GeoEntity {

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EarthVeinRunesEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<EarthVeinRunesEntity>("idle_ctrl", 0,
            state -> {
                state.setAnimation(ANIM_IDLE);
                return PlayState.CONTINUE;
            }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
