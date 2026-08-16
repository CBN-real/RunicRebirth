package com.github.runicrebirth.entities;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class ArcaneDroneEntity extends Entity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("cast");

    private static final int BEAM_COOLDOWN_TICKS = 100;
    private static final double ATTACK_RANGE = 16.0;

    private static final EntityDataAccessor<Boolean> DATA_CASTING =
        SynchedEntityData.defineId(ArcaneDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
        SynchedEntityData.defineId(ArcaneDroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_FACING_YAW =
        SynchedEntityData.defineId(ArcaneDroneEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private int beamCooldown = BEAM_COOLDOWN_TICKS;
    private int lastTargetId = -1;

    public ArcaneDroneEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CASTING, false);
        builder.define(DATA_OWNER_ID, -1);
        builder.define(DATA_FACING_YAW, 0f);
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }



    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            int ownerId = entityData.get(DATA_OWNER_ID);
            if (ownerId != -1) {
                Entity owner = level().getEntity(ownerId);
                if (owner instanceof Player living) {
                    Vec3 prev = hoverPosFromOld(living);
                    Vec3 cur = hoverPos(living);
                    this.xo = prev.x;
                    this.yo = prev.y;
                    this.zo = prev.z;
//                    this.setPos(cur.x, cur.y, cur.z);
//                    this.setYRot(entityData.get(DATA_FACING_YAW));
                    this.lerpPositionAndRotationStep(2, cur.x, cur.y, cur.z, entityData.get(DATA_FACING_YAW), owner.getXRot());
                }
            }
            return;
        }

        if (!(level() instanceof ServerLevel serverLevel)) return;

        ServerPlayer owner = findOwner(serverLevel);
        if (owner == null || !owner.isAlive()) {
            discard();
            return;
        }

        entityData.set(DATA_OWNER_ID, owner.getId());

        Vec3 hover = hoverPos(owner);
        this.setPos(hover.x, hover.y, hover.z);

        if (this.tickCount % 4 == 0) {
            serverLevel.sendParticles(new ScaledParticleOption(ModParticles.ARCANE_TINY.get(), 0.5f),
                getX(), getY() + 0.2, getZ(), 1, 0.1, 0.1, 0.1, 0.0);
        }

        if (beamCooldown > 0) {
            beamCooldown--;
            if (entityData.get(DATA_CASTING) && beamCooldown < BEAM_COOLDOWN_TICKS - 15) {
                entityData.set(DATA_CASTING, false);
            }
            Entity lastTarget = serverLevel.getEntity(lastTargetId);
            float yaw;
            if (lastTarget instanceof LivingEntity lt && lt.isAlive()
                    && this.distanceToSqr(lt) <= ATTACK_RANGE * ATTACK_RANGE) {
                yaw = yawToward(this.position(), lt.getBoundingBox().getCenter());
            } else {
                yaw = owner.getYRot();
                lastTargetId = -1;
            }
            entityData.set(DATA_FACING_YAW, yaw);
            return;
        }

        LivingEntity beamTarget = findTarget(serverLevel, owner);
        if (beamTarget == null) {
            beamCooldown = 20;
            entityData.set(DATA_FACING_YAW, owner.getYRot());
            return;
        }

        lastTargetId = beamTarget.getId();
        float targetYaw = yawToward(this.position(), beamTarget.getBoundingBox().getCenter());
        entityData.set(DATA_FACING_YAW, targetYaw);
        fireBeam(serverLevel, owner, beamTarget);
        entityData.set(DATA_CASTING, true);
        beamCooldown = BEAM_COOLDOWN_TICKS;
    }

    private static float yawToward(Vec3 from, Vec3 to) {
        return (float) Math.toDegrees(Math.atan2(-(to.x - from.x), to.z - from.z));
    }

    private void fireBeam(ServerLevel level, ServerPlayer owner, LivingEntity target) {
        Vec3 start = this.position().add(0, 0.2, 0);
        Vec3 dir = target.getBoundingBox().getCenter().subtract(start).normalize();

        SpellCastContext ctx = new SpellCastContext(level, owner, ItemStack.EMPTY, start, dir, 0f, 0f, target);
        SpellParams params = new SpellParams(4f, 0.4f,0.25f, 1.0f, 0, 0, 0, ModElements.ARCANE.get(), MagicDamageType.SPIRIT);
        ModSpellTypes.MAGIC_BEAM.get().onCast(ctx, params);
    }

    private LivingEntity findTarget(ServerLevel level, ServerPlayer owner) {
        AABB box = AABB.ofSize(this.position(), ATTACK_RANGE * 2, ATTACK_RANGE * 2, ATTACK_RANGE * 2);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, box,
            e -> e != owner && e.isAlive() && e.getType().getCategory() == MobCategory.MONSTER);

        LivingEntity best = null;
        double bestDist = ATTACK_RANGE * ATTACK_RANGE;
        for (LivingEntity e : candidates) {
            double dist = this.distanceToSqr(e);
            if (dist < bestDist) {
                bestDist = dist;
                best = e;
            }
        }
        return best;
    }

    private ServerPlayer findOwner(ServerLevel level) {
        if (ownerUUID == null) return null;
        return level.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    private Vec3 hoverPos(LivingEntity player) {
        float yawRad = (float) Math.toRadians(player.getYRot());
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);
        return player.position()
            .add(rightX * 0.7, player.getBbHeight() + 0.5, rightZ * 0.7);
    }

    private Vec3 hoverPosFromOld(LivingEntity player) {
        float yawRad = (float) Math.toRadians(player.getYRot());
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);
        return new Vec3(player.xo, player.yo, player.zo)
            .add(rightX * 0.7, player.getBbHeight() + 0.5, rightZ * 0.7);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "drone_ctrl", 5, state -> {
            if (entityData.get(DATA_CASTING)) {
                state.setAnimation(IDLE);
                return PlayState.CONTINUE;
            }
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        super.lerpTo(x, y, z, yRot, xRot, steps);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void push(double x, double y, double z) {}
}
