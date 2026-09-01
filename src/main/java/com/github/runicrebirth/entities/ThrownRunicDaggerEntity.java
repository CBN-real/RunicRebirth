package com.github.runicrebirth.entities;

import com.github.runicrebirth.capabilities.magic.MagicData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class ThrownRunicDaggerEntity extends Entity implements GeoEntity {

    public enum Phase { SPINNING, STUCK_ENTITY, STUCK_BLOCK, RETURNING }

    private static final RawAnimation ANIM_SPINNING  = RawAnimation.begin().thenLoop("spinning");
    private static final RawAnimation ANIM_IDLE      = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_RETURNING = RawAnimation.begin().thenLoop("returning");

    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(ThrownRunicDaggerEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private int ownerEntityId = -1;
    private Vec3 stuckOffset = Vec3.ZERO;
    private int stuckTargetId = -1;
    private int spinningAge = 0;
    private float stuckEntityYaw = 0f;
    private float stuckDaggerYaw = 0f;

    public ThrownRunicDaggerEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PHASE, Phase.SPINNING.ordinal());
    }

    public Phase getPhase() {
        return Phase.values()[entityData.get(DATA_PHASE)];
    }

    public void setPhase(Phase phase) {
        entityData.set(DATA_PHASE, phase.ordinal());
    }

    public void setOwner(ServerPlayer player) {
        this.ownerUUID = player.getUUID();
        this.ownerEntityId = player.getId();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        switch (getPhase()) {
            case SPINNING  -> tickSpinning();
            case STUCK_ENTITY -> tickStuckEntity();
            case STUCK_BLOCK  -> { /* wait for signal */ }
            case RETURNING -> tickReturning();
        }
    }

    private void tickSpinning() {
        spinningAge++;
        Vec3 motion = getDeltaMovement();
        motion = motion.add(0, -0.03, 0);
        setDeltaMovement(motion);
        updateRotationFromVelocity(motion);

        Vec3 pos = position();
        Vec3 nextPos = pos.add(motion);

        ClipContext clipCtx = new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult blockHit = level().clip(clipCtx);
        if (blockHit.getType() != HitResult.Type.MISS) {
            setPhase(Phase.STUCK_BLOCK);
            setDeltaMovement(Vec3.ZERO);
            Vec3 embedDir = motion.normalize();
            setPos(
                blockHit.getLocation().x - embedDir.x * 0.5,
                blockHit.getLocation().y - embedDir.y * 0.5,
                blockHit.getLocation().z - embedDir.z * 0.5
            );
            notifyOwner();
            return;
        }

        AABB searchBox = getBoundingBox().move(motion).inflate(0.3);
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, searchBox,
            e -> e.getId() != ownerEntityId && e.isAlive());
        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(0);
            stuckTargetId = target.getId();
            stuckOffset = new Vec3(0, target.getBbHeight() * 0.4, 0);
            stuckEntityYaw = target.getYRot();
            stuckDaggerYaw = getYRot();
            setPhase(Phase.STUCK_ENTITY);
            setDeltaMovement(Vec3.ZERO);
            ServerPlayer owner = findOwner();
            if (owner != null) {
                float damage = (float) (owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) * 1.5);
                target.hurt(level().damageSources().playerAttack(owner), damage);
            }
            notifyOwner();
            return;
        }

        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
        this.lerpPositionAndRotationStep(2, nextPos.x, nextPos.y, nextPos.z, getYRot(), getXRot());

        if (spinningAge > 100) {
            clearOwnerDagger();
            discard();
        }
    }

    private void tickStuckEntity() {
        Entity target = level().getEntity(stuckTargetId);
        if (target == null || !target.isAlive()) {
            setPhase(Phase.STUCK_BLOCK);
            return;
        }
        float deltaYaw = target.getYRot() - stuckEntityYaw;
        double rad = Math.toRadians(deltaYaw);
        double rotX = stuckOffset.x * Math.cos(rad) - stuckOffset.z * Math.sin(rad);
        double rotZ = stuckOffset.x * Math.sin(rad) + stuckOffset.z * Math.cos(rad);
        Vec3 rotatedOffset = new Vec3(rotX, stuckOffset.y, rotZ);
        Vec3 newPos = target.position().add(rotatedOffset);
        setPos(newPos.x, newPos.y, newPos.z);
        setYRot(stuckDaggerYaw + deltaYaw);
    }

    private void tickReturning() {
        ServerPlayer owner = findOwner();
        if (owner == null) {
            clearOwnerDagger();
            discard();
            return;
        }
        Vec3 ownerPos = owner.getEyePosition();
        Vec3 dir = ownerPos.subtract(position()).normalize();
        Vec3 vel = dir.scale(1.5);
        setDeltaMovement(vel);
        updateRotationFromVelocity(vel);
        Vec3 next = position().add(getDeltaMovement());
        setPos(next.x, next.y, next.z);

        if (position().distanceTo(ownerPos) < 1.5) {
            clearOwnerDagger();
            discard();
        }
    }

    private void updateRotationFromVelocity(Vec3 vel) {
        double hDist = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (hDist < 0.001 && Math.abs(vel.y) < 0.001) return;
        float yaw = (float) Math.toDegrees(Math.atan2(vel.x, -vel.z));
        float pitch = (float) Math.toDegrees(Math.atan2(-vel.y, hDist));
        setYRot(yaw);
        setXRot(pitch);
    }

    private void notifyOwner() {
        if (!(level() instanceof ServerLevel)) return;
        ServerPlayer owner = findOwner();
        if (owner != null) {
            MagicData.of(owner).setThrownDaggerEntityId(getId());
        }
    }

    private void clearOwnerDagger() {
        if (!(level() instanceof ServerLevel)) return;
        ServerPlayer owner = findOwner();
        if (owner != null) {
            MagicData.of(owner).clearThrownDaggerEntityId();
            com.github.runicrebirth.network.DaggerAnimS2CPacket.send(
                owner, com.github.runicrebirth.network.DaggerAnimS2CPacket.Anim.IDLE);
        }
    }

    private ServerPlayer findOwner() {
        if (!(level() instanceof ServerLevel sl)) return null;
        if (ownerUUID == null) return null;
        return sl.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ThrownRunicDaggerEntity>("phase_ctrl", 0, state -> {
            return switch (getPhase()) {
                case SPINNING -> { state.setAnimation(ANIM_SPINNING); yield PlayState.CONTINUE; }
                case STUCK_ENTITY, STUCK_BLOCK -> { state.setAnimation(ANIM_IDLE); yield PlayState.CONTINUE; }
                case RETURNING -> { state.setAnimation(ANIM_RETURNING); yield PlayState.CONTINUE; }
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
