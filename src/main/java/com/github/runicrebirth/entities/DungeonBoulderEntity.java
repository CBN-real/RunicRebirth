package com.github.runicrebirth.entities;

import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class DungeonBoulderEntity extends Entity implements GeoEntity {

    private static final RawAnimation ROLLING = RawAnimation.begin().thenLoop("rolling");
    private static final float SPEED = 0.4f;
    private static final float GRAVITY = 0.08f;
    private static final float BLUNT_DAMAGE = 40.0f;
    private static final int LIFESPAN = 200;

    private static final EntityDataAccessor<Integer> TRAVEL_DIR =
            SynchedEntityData.defineId(DungeonBoulderEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Direction travelDirection = Direction.NORTH;
    // Pass through spawner block for first 3 ticks to avoid collision on spawn
    private int spawnGraceTicks = 3;

    public DungeonBoulderEntity(EntityType<DungeonBoulderEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public static DungeonBoulderEntity create(Level level, Direction direction) {
        DungeonBoulderEntity entity = new DungeonBoulderEntity(ModEntities.DUNGEON_BOULDER.get(), level);
        entity.travelDirection = direction;
        entity.entityData.set(TRAVEL_DIR, direction.get2DDataValue());
        return entity;
    }

    public Direction getTravelDirection() {
        return Direction.from2DDataValue(entityData.get(TRAVEL_DIR));
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;

        if (tickCount > LIFESPAN) {
            discard();
            return;
        }

        if (spawnGraceTicks > 0) {
            spawnGraceTicks--;
            noPhysics = true;
            setDeltaMovement(0, -GRAVITY, 0);
            move(MoverType.SELF, getDeltaMovement());
            return;
        }
        noPhysics = false;

        Vec3 motion = getDeltaMovement();
        double newY = onGround() ? 0.0 : Math.max(motion.y - GRAVITY, -0.78);

        double dx = onGround() ? travelDirection.getStepX() * SPEED : 0.0;
        double dz = onGround() ? travelDirection.getStepZ() * SPEED : 0.0;

        setDeltaMovement(dx, newY, dz);
        move(MoverType.SELF, getDeltaMovement());

        AABB bb = getBoundingBox();
        DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPosition(blockPosition());
        float bluntMult = inst != null ? inst.getBluntTrapMultiplier() : 1.0f;
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, bb);
        for (LivingEntity target : targets) {
            target.hurt(level().damageSources().generic(), BLUNT_DAMAGE * bluntMult);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TRAVEL_DIR, Direction.NORTH.get2DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("TravelDirection")) {
            travelDirection = Direction.byName(tag.getString("TravelDirection"));
            if (travelDirection == null) travelDirection = Direction.NORTH;
            entityData.set(TRAVEL_DIR, travelDirection.get2DDataValue());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("TravelDirection", travelDirection.getName());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                state -> state.setAndContinue(ROLLING)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
