package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
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

    private static final int FINISH_TICKS = 0;

    private static final RawAnimation INITIATE_SPELL = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    private static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractSpellCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_MODIFIER_IDS =
        SynchedEntityData.defineId(AbstractSpellCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_FINISHING =
        SynchedEntityData.defineId(AbstractSpellCircleEntity.class, EntityDataSerializers.BOOLEAN);

    protected LivingEntity owner;
    protected SpellParams params;
    protected LivingEntity target;
    private int age;
    protected int lifespan;
    private int finishingTicks;
    private boolean projectileSpawned;

    protected AbstractSpellCircleEntity(EntityType<? extends AbstractSpellCircleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    protected void init(LivingEntity owner, SpellParams params, int lifespan, LivingEntity target) {
        this.owner = owner;
        this.lifespan = lifespan;
        this.target = target;
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
        this.entityData.set(DATA_MODIFIER_IDS, String.join(",", params.modifierIds));
        this.params = params.copy();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
        builder.define(DATA_MODIFIER_IDS, "");
        builder.define(DATA_FINISHING, false);
    }

    public String getElementId() {
        return this.entityData.get(DATA_ELEMENT);
    }

    public String getModifierIds() {
        return this.entityData.get(DATA_MODIFIER_IDS);
    }

    public boolean hasModifier(String modPath) {
        String mods = getModifierIds();
        if (mods.isEmpty()) return false;
        for (String id : mods.split(",")) {
            if (id.equals(modPath)) return true;
        }
        return false;
    }

    public float getCircleScale() {
        if (hasModifier("size_plus_four")) return 3.0f;
        if (hasModifier("size_plus_two")) return 2.0f;
        if (hasModifier("size_plus")) return 1.5f;
        return 1.0f;
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
        if (age == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_SPAWN_CIRCLE.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
            onSpawn();
            //spawnCrackling((ServerLevel) level());
        }
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

    private void spawnCrackling(ServerLevel serverLevel) {
        float scale = getCircleScale();
        int color = params.element.displayColor();
        EnergyCracklingEntity crackling = new EnergyCracklingEntity(serverLevel, scale * 0.15f, color, lifespan - 10, 0.5f, 1.0f, 0.15f * scale);
        var center = getBoundingBox().getCenter();
        crackling.setPos(center.x, center.y, center.z);
        crackling.attachTo(this);
        serverLevel.addFreshEntity(crackling);
    }

    protected Vec3 getCircleSpawnPos(float baseYOffset) {
        return new Vec3(getX(), getY() + baseYOffset * getCircleScale(), getZ());
    }

    protected void onSpawn() {}

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
