package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EnergyCracklingEntity extends Entity {

    private static final EntityDataAccessor<Float> DATA_RADIUS =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ATTACHED_ID =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DENSITY =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SPEED =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_THICKNESS =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_X =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Y =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Z =
        SynchedEntityData.defineId(EnergyCracklingEntity.class, EntityDataSerializers.FLOAT);

    private int duration = 1;

    public EnergyCracklingEntity(EntityType<? extends EnergyCracklingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public EnergyCracklingEntity(Level level, float radius, int colorRgb, int duration,
                                  float density, float speed, float thickness) {
        this(ModEntities.ENERGY_CRACKLING.get(), level);
        this.duration = duration;
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_COLOR, colorRgb);
        this.entityData.set(DATA_DENSITY, density);
        this.entityData.set(DATA_SPEED, speed);
        this.entityData.set(DATA_THICKNESS, thickness);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_RADIUS, 1.0f);
        builder.define(DATA_COLOR, 0x4488FF);
        builder.define(DATA_ATTACHED_ID, -1);
        builder.define(DATA_DENSITY, 1.0f);
        builder.define(DATA_SPEED, 1.0f);
        builder.define(DATA_THICKNESS, 1.0f);
        builder.define(DATA_OFFSET_X, 0.0f);
        builder.define(DATA_OFFSET_Y, 0.0f);
        builder.define(DATA_OFFSET_Z, 0.0f);
    }

    public void setOffset(float x, float y, float z) {
        this.entityData.set(DATA_OFFSET_X, x);
        this.entityData.set(DATA_OFFSET_Y, y);
        this.entityData.set(DATA_OFFSET_Z, z);
    }

    public void attachTo(Entity entity) {
        this.entityData.set(DATA_ATTACHED_ID, entity.getId());
    }

    public int getAttachedEntityId() {
        return this.entityData.get(DATA_ATTACHED_ID);
    }

    public float getCrackleRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public int getColor() {
        return this.entityData.get(DATA_COLOR);
    }

    /** Multiplier for bolt count. 1 = 7 bolts, 2 = 14, 0.5 = 3. */
    public float getDensity() {
        return this.entityData.get(DATA_DENSITY);
    }

    /** Multiplier for flicker rate. 1 = change every 2 ticks, 2 = every tick, 0.5 = every 4 ticks. */
    public float getSpeed() {
        return this.entityData.get(DATA_SPEED);
    }

    /** Multiplier for tube width. 1 = default, 2 = twice as thick. */
    public float getThickness() {
        return this.entityData.get(DATA_THICKNESS);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;

        if (--duration <= 0) {
            discard();
            return;
        }

        int id = getAttachedEntityId();
        if (id != -1) {
            Entity attached = level().getEntity(id);
            if (attached == null || attached.isRemoved()) {
                discard();
                return;
            }
            var center = attached.getBoundingBox().getCenter();
            this.setPos(
                center.x + this.entityData.get(DATA_OFFSET_X),
                center.y + this.entityData.get(DATA_OFFSET_Y),
                center.z + this.entityData.get(DATA_OFFSET_Z));
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
