package com.github.runicrebirth.entities;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.network.MagicHandSyncS2CPacket;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MagicHandEntity extends Entity implements GeoEntity {

    private static final RawAnimation INITIATE = RawAnimation.begin().thenPlay("initiate_spell");
    private static final RawAnimation HOLD = RawAnimation.begin().thenLoop("hold_spell");
    private static final RawAnimation END = RawAnimation.begin().thenPlayAndHold("end_spell");

    public static final int PHASE_INITIATING = 0;
    public static final int PHASE_HOLDING = 1;
    public static final int PHASE_ENDING = 2;

    public static final ResourceLocation COOLDOWN_KEY =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_hand_ring");
    private static final int COOLDOWN_TICKS = 100;

    public static final int HOSTILE_HOLD_TICKS = 40;
    private static final int INITIATE_TICKS = 18;
    private static final int END_TICKS = 20;
    private static final double THROW_SPEED = 1.5;

    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(MagicHandEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID =
        SynchedEntityData.defineId(MagicHandEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_PASSIVE =
        SynchedEntityData.defineId(MagicHandEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private double holdDistance;
    private int phaseAge;
    private int holdingAge;
    private boolean targetStateRestored;

    public MagicHandEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static MagicHandEntity create(ServerPlayer player, LivingEntity target, boolean isPassive) {
        MagicHandEntity entity = new MagicHandEntity(ModEntities.MAGIC_HAND.get(), player.level());
        entity.ownerUUID = player.getUUID();
        entity.entityData.set(DATA_TARGET_ID, target.getId());
        entity.entityData.set(DATA_IS_PASSIVE, isPassive);
        entity.holdDistance = player.getEyePosition().distanceTo(target.getBoundingBox().getCenter());
        entity.setPos(target.getX(), target.getY() + target.getBbHeight(), target.getZ());
        return entity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PHASE, PHASE_INITIATING);
        builder.define(DATA_TARGET_ID, -1);
        builder.define(DATA_IS_PASSIVE, false);
    }

    public int getPhase() { return entityData.get(DATA_PHASE); }
    public boolean isPassive() { return entityData.get(DATA_IS_PASSIVE); }

    private void setPhase(int phase) {
        entityData.set(DATA_PHASE, phase);
        phaseAge = 0;
    }

    public void beginEnding() {
        if (getPhase() == PHASE_ENDING) return;
        applyThrowMomentum();
        restoreTargetState();
        setPhase(PHASE_ENDING);
        if (level() instanceof ServerLevel serverLevel && ownerUUID != null) {
            ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
            if (owner != null) {
                MagicData.of(owner).startCooldown(COOLDOWN_KEY, COOLDOWN_TICKS);
                MagicHandSyncS2CPacket.sendTo(owner, 0, false);
            }
        }
    }

    private void applyThrowMomentum() {
        if (ownerUUID == null || !(level() instanceof ServerLevel serverLevel)) return;
        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (owner == null) return;
        int targetId = entityData.get(DATA_TARGET_ID);
        Entity targetEntity = level().getEntity(targetId);
        if (!(targetEntity instanceof LivingEntity target)) return;
        Vec3 throwVel = owner.getLookAngle().scale(THROW_SPEED);
        // don't throw into a block directly in front
        AABB movedAABB = target.getBoundingBox().move(throwVel.scale(0.5));
        if (!level().noCollision(target, movedAABB)) throwVel = Vec3.ZERO;
        target.setDeltaMovement(throwVel);
        target.hurtMarked = true;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (level().isClientSide || ownerUUID == null) return;
        if (!(level() instanceof ServerLevel serverLevel)) return;
        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (owner != null) {
            MagicData data = MagicData.of(owner);
            if (data.magicHandEntityId() == this.getId()) data.clearMagicHandEntityId();
        }
    }

    private void restoreTargetState() {
        if (targetStateRestored) return;
        targetStateRestored = true;
        int targetId = entityData.get(DATA_TARGET_ID);
        if (targetId == -1) return;
        Entity target = level().getEntity(targetId);
        if (target instanceof LivingEntity living) {
            living.setNoGravity(false);
            if (living instanceof Mob mob) mob.setNoAi(false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        int phase = getPhase();

        if (phase == PHASE_INITIATING) {
            phaseAge++;
            int initTargetId = entityData.get(DATA_TARGET_ID);
            Entity initTarget = level().getEntity(initTargetId);
            if (initTarget != null) setPos(initTarget.getX(), initTarget.getY() + initTarget.getBbHeight(), initTarget.getZ());
            if (phaseAge >= INITIATE_TICKS) setPhase(PHASE_HOLDING);

        } else if (phase == PHASE_HOLDING) {
            holdingAge++;
            tickHolding();

        } else if (phase == PHASE_ENDING) {
            phaseAge++;
            if (phaseAge >= END_TICKS) discard();
        }
    }

    private void tickHolding() {
        if (ownerUUID == null) { beginEnding(); return; }
        if (!(level() instanceof ServerLevel serverLevel)) { beginEnding(); return; }

        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (owner == null || !owner.isAlive()) { beginEnding(); return; }

        int targetId = entityData.get(DATA_TARGET_ID);
        Entity targetEntity = level().getEntity(targetId);
        if (!(targetEntity instanceof LivingEntity target) || !target.isAlive()) {
            beginEnding(); return;
        }

        if (target instanceof Mob mob) mob.setNoAi(true);
        target.setNoGravity(true);

        Vec3 eyePos = owner.getEyePosition();
        Vec3 lookDir = owner.getLookAngle();
        double halfH = target.getBbHeight() / 2.0;
        double halfW = target.getBbWidth() / 2.0;
        // back off enough so entity BB doesn't clip into the block surface
        double backoff = Math.max(halfH, halfW) + 0.2;

        Vec3 desiredCenter = eyePos.add(lookDir.scale(holdDistance));

        BlockHitResult blockHit = level().clip(new ClipContext(
            eyePos, desiredCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
        Vec3 safeCenter = blockHit.getType() == HitResult.Type.BLOCK
            ? blockHit.getLocation().subtract(lookDir.scale(backoff))
            : desiredCenter;

        Vec3 safeFeet = new Vec3(safeCenter.x, safeCenter.y - halfH, safeCenter.z);

        // Push up if entity BB clips floor/ceiling blocks
        AABB proposedAABB = new AABB(
            safeFeet.x - halfW, safeFeet.y, safeFeet.z - halfW,
            safeFeet.x + halfW, safeFeet.y + target.getBbHeight(), safeFeet.z + halfW);
        if (!level().noCollision(target, proposedAABB)) {
            boolean cleared = false;
            for (double dy = 0.25; dy <= target.getBbHeight() + 1.0; dy += 0.25) {
                if (level().noCollision(target, proposedAABB.move(0, dy, 0))) {
                    safeFeet = new Vec3(safeFeet.x, safeFeet.y + dy, safeFeet.z);
                    cleared = true;
                    break;
                }
            }
            if (!cleared) {
                safeFeet = new Vec3(target.getX(), target.getY(), target.getZ());
            }
        }

        target.teleportTo(safeFeet.x, safeFeet.y, safeFeet.z);
        target.setDeltaMovement(Vec3.ZERO);

        // hand tracks top of held entity
        this.setPos(target.getX(), target.getY() + target.getBbHeight(), target.getZ());

        serverLevel.sendParticles(new ScaledParticleOption(ModParticles.ARCANE_ELEMENT.get(), 0.8f),
            this.getX(), this.getY(), this.getZ(), 1, 0.15, 0.15, 0.15, 0.0);

        if (!isPassive() && holdingAge >= HOSTILE_HOLD_TICKS) beginEnding();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "magic_hand", 0, state -> {
            int phase = getPhase();
            if (phase == PHASE_INITIATING) {
                state.setAnimation(INITIATE);
            } else if (phase == PHASE_HOLDING) {
                state.setAnimation(HOLD);
            } else {
                state.setAnimation(END);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.setPos(x, y, z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void push(double x, double y, double z) {}
}
