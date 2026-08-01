package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ArcaneTetherEntity extends Entity {

    public static final float MAX_RANGE = 24.0f;
    private static final float PULL_FORCE = 0.10f;
    private static final float MAX_PULL_SPEED = 0.55f;
    private static final float SLACK = 2.0f;

    private static final EntityDataAccessor<Float> DATA_TARGET_X =
        SynchedEntityData.defineId(ArcaneTetherEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TARGET_Y =
        SynchedEntityData.defineId(ArcaneTetherEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TARGET_Z =
        SynchedEntityData.defineId(ArcaneTetherEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ENTITY_ID =
        SynchedEntityData.defineId(ArcaneTetherEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ATTACHED_ENTITY_ID =
        SynchedEntityData.defineId(ArcaneTetherEntity.class, EntityDataSerializers.INT);

    private UUID ownerUUID;

    public ArcaneTetherEntity(EntityType<? extends ArcaneTetherEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public ArcaneTetherEntity(Level level, UUID ownerUUID, float targetX, float targetY, float targetZ, int attachedEntityId) {
        this(ModEntities.ARCANE_TETHER.get(), level);
        this.ownerUUID = ownerUUID;
        this.entityData.set(DATA_TARGET_X, targetX);
        this.entityData.set(DATA_TARGET_Y, targetY);
        this.entityData.set(DATA_TARGET_Z, targetZ);
        this.entityData.set(DATA_ATTACHED_ENTITY_ID, attachedEntityId);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGET_X, 0f);
        builder.define(DATA_TARGET_Y, 0f);
        builder.define(DATA_TARGET_Z, 0f);
        builder.define(DATA_OWNER_ENTITY_ID, -1);
        builder.define(DATA_ATTACHED_ENTITY_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (!(level() instanceof ServerLevel serverLevel)) return;

        if (ownerUUID == null) { discard(); return; }
        net.minecraft.world.entity.player.Player rawOwner = serverLevel.getPlayerByUUID(ownerUUID);
        if (!(rawOwner instanceof ServerPlayer owner) || owner.isRemoved()) { discard(); return; }

        // Sync owner entity ID for client rendering
        entityData.set(DATA_OWNER_ENTITY_ID, owner.getId());

        // Update target if attached to entity
        int attachedId = entityData.get(DATA_ATTACHED_ENTITY_ID);
        if (attachedId != -1) {
            Entity attached = level().getEntity(attachedId);
            if (attached == null || attached.isRemoved()) { discard(); return; }
            Vec3 center = attached.getBoundingBox().getCenter();
            entityData.set(DATA_TARGET_X, (float) center.x);
            entityData.set(DATA_TARGET_Y, (float) center.y);
            entityData.set(DATA_TARGET_Z, (float) center.z);
            setPos(center.x, center.y, center.z);
        }

        Vec3 targetPos = getTargetPos();
        Vec3 playerEye = owner.getEyePosition();
        double dist = playerEye.distanceTo(targetPos);

        if (dist > MAX_RANGE) {
            discard();
            return;
        }

        if (dist > SLACK) {
            Vec3 pullDir = targetPos.subtract(playerEye).normalize();
            // Proportional spring: scale with overstretch, capped so player can't accelerate past MAX_PULL_SPEED
            double overstretch = dist - SLACK;
            double forceMag = Math.min(overstretch * PULL_FORCE, MAX_PULL_SPEED);
            Vec3 motion = owner.getDeltaMovement();
            double currentSpeed = motion.dot(pullDir);
            if (currentSpeed < forceMag) {
                owner.setDeltaMovement(motion.add(pullDir.scale(forceMag - currentSpeed)));
                // Server-set deltaMovement is NOT auto-synced for players — send packet manually
                owner.connection.send(new ClientboundSetEntityMotionPacket(owner.getId(), owner.getDeltaMovement()));
            }
            owner.resetFallDistance();
        }
    }

    public Vec3 getTargetPos() {
        return new Vec3(entityData.get(DATA_TARGET_X), entityData.get(DATA_TARGET_Y), entityData.get(DATA_TARGET_Z));
    }

    public int getOwnerEntityId() {
        return entityData.get(DATA_OWNER_ENTITY_ID);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
    }
}
