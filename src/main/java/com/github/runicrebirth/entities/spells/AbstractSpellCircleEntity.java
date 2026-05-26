package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModElements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractSpellCircleEntity extends Entity implements GeoEntity {

    private static final int FINISH_TICKS = 40;

    private static final RawAnimation INITIATE_SPELL = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    private static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractSpellCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_FINISHING =
        SynchedEntityData.defineId(AbstractSpellCircleEntity.class, EntityDataSerializers.BOOLEAN);

    protected LivingEntity owner;
    protected SpellParams params;
    private int age;
    private int lifespan;
    private int finishingTicks;
    private boolean projectileSpawned;

    protected AbstractSpellCircleEntity(EntityType<? extends AbstractSpellCircleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    protected void init(LivingEntity owner, SpellParams params, int lifespan) {
        this.owner = owner;
        this.params = params;
        this.lifespan = lifespan;
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
        builder.define(DATA_FINISHING, false);
    }

    public String getElementId() {
        return this.entityData.get(DATA_ELEMENT);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if (entityData.get(DATA_FINISHING)) {
            if (--finishingTicks <= 0) discard();
            return;
        }

        age++;
        if (age >= lifespan || owner == null || !owner.isAlive()) {
            beginFinishing();
            return;
        }

        if (!projectileSpawned && level() instanceof ServerLevel) {
            spawnProjectile();
            projectileSpawned = true;
        }
    }

    private void beginFinishing() {
        finishingTicks = FINISH_TICKS;
        entityData.set(DATA_FINISHING, true);
    }

    protected abstract void spawnProjectile();

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "form", 0, state -> {
            if (state.getAnimatable().entityData.get(DATA_FINISHING)) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_SPELL);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
