package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.Identifier;
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

    public static final Identifier COOLDOWN_KEY =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "arcane_tether_ring");
    public static final int COOLDOWN_TICKS = 20;

    public static final float MAX_RANGE = 24.0f;
    private static final float PULL_FORCE = 0.10f;
    private static final float MAX_PULL_SPEED = 0.35f;
    private static final float SLACK = 1.5f;
    // Pull downward (with gravity) feels much stronger; upward unchanged
    private static final float GRAVITY_PULL_MULT = 3.0f;
    // Amplify horizontal momentum each tethered tick for pendulum swing feel
    private static final float SWING_BOOST = 1.15f;
    private static final double MAX_SWING_SPEED = 0.8;
    // Force tapers to 0 over this range above SLACK to avoid snapping into target
    private static final float NEAR_FADE_RANGE = 3.0f;

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
        if (level().isClientSide()) return;
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
            double overstretch = dist - SLACK;

            // Pulling downward = gravity-aided; pulling upward = same force
            double gravMult = pullDir.y < 0 ? GRAVITY_PULL_MULT : 1.0;
            double maxSpeed = MAX_PULL_SPEED * gravMult;
            double nearFade = Math.min(1.0, overstretch / NEAR_FADE_RANGE);
            double forceMag = Math.min(overstretch * PULL_FORCE * gravMult, maxSpeed);

            Vec3 motion = owner.getDeltaMovement();

            // Amplify horizontal momentum for pendulum swing; cap to prevent runaway
            double bx = Math.copySign(Math.min(Math.abs(motion.x) * Math.max(SWING_BOOST * nearFade, 1.025f), MAX_SWING_SPEED), motion.x);
            double bz = Math.copySign(Math.min(Math.abs(motion.z) * Math.max(SWING_BOOST * nearFade, 1.025f), MAX_SWING_SPEED), motion.z);
            motion = new Vec3(bx, motion.y, bz);

            double currentSpeed = motion.dot(pullDir);
            if (currentSpeed < forceMag) {
                motion = motion.add(pullDir.scale(forceMag - currentSpeed));
            }
            owner.setDeltaMovement(motion);
            // Server-set deltaMovement is NOT auto-synced for players — send packet manually
            owner.connection.send(new ClientboundSetEntityMotionPacket(owner.getId(), owner.getDeltaMovement()));
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
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (reason != Entity.RemovalReason.DISCARDED) return;
        if (level().isClientSide() || ownerUUID == null) return;
        if (!(level() instanceof ServerLevel serverLevel)) return;
        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (owner == null) return;
        MagicData.of(owner).startCooldown(COOLDOWN_KEY, COOLDOWN_TICKS);
    }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}
}
