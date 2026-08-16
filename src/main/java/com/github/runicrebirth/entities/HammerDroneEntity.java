package com.github.runicrebirth.entities;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModParticles;
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
import net.minecraft.world.entity.MobCategory;
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

public class HammerDroneEntity extends Entity implements GeoEntity {

    public static final int PHASE_HOVERING = 0;
    public static final int PHASE_FLYING = 1;
    public static final int PHASE_ATTACKING = 2;
    public static final int PHASE_RETURNING = 3;

    private static final int ATTACK_COOLDOWN = 80;
    private static final int ATTACK_ANIM_TICKS = 12;
    private static final int DAMAGE_TICK = 6;
    private static final float ATTACK_DAMAGE = 6.0f;
    private static final double ATTACK_RANGE_PLAYER = 6.0;
    private static final double FLY_SPEED = 0.4;
    private static final double CLOSE_THRESHOLD_SQ = 1.0;

    public static final ResourceLocation DRONE_ID =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "hammer_drone");

    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(HammerDroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
        SynchedEntityData.defineId(HammerDroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_FACING_YAW =
        SynchedEntityData.defineId(HammerDroneEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private int attackCooldown = 0;
    private int phaseAge = 0;
    private int targetEntityId = -1;
    private boolean damageDealt = false;

    public HammerDroneEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PHASE, PHASE_HOVERING);
        builder.define(DATA_OWNER_ID, -1);
        builder.define(DATA_FACING_YAW, 0f);
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public int getPhase() {
        return entityData.get(DATA_PHASE);
    }

    private void setPhase(int phase) {
        entityData.set(DATA_PHASE, phase);
        phaseAge = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            int ownerId = entityData.get(DATA_OWNER_ID);
            if (ownerId != -1 && getPhase() == PHASE_HOVERING) {
                Entity owner = level().getEntity(ownerId);
                if (owner instanceof LivingEntity living) {
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
        phaseAge++;

        switch (getPhase()) {
            case PHASE_HOVERING -> tickHovering(serverLevel, owner);
            case PHASE_FLYING -> tickFlying(serverLevel, owner);
            case PHASE_ATTACKING -> tickAttacking(serverLevel, owner);
            case PHASE_RETURNING -> tickReturning(owner);
        }

        if (this.tickCount % 5 == 0) {
            serverLevel.sendParticles(new ScaledParticleOption(ModParticles.EARTH_TINY.get(), 0.4f),
                getX(), getY() + 0.1, getZ(), 1, 0.08, 0.08, 0.08, 0.0);
        }
    }

    private static float yawToward(Vec3 from, Vec3 to) {
        return (float) Math.toDegrees(Math.atan2(-(to.x - from.x), to.z - from.z));
    }

    private void tickHovering(ServerLevel level, ServerPlayer owner) {
        Vec3 hover = hoverPos(owner);
        setPos(hover.x, hover.y, hover.z);

        if (attackCooldown > 0) {
            attackCooldown--;
            Entity lastTarget = level.getEntity(targetEntityId);
            float yaw = (lastTarget instanceof LivingEntity lt && lt.isAlive())
                ? yawToward(this.position(), lt.getBoundingBox().getCenter())
                : owner.getYRot();
            this.setYRot(yaw);
            entityData.set(DATA_FACING_YAW, yaw);
            return;
        }

        this.setYRot(owner.getYRot());
        entityData.set(DATA_FACING_YAW, owner.getYRot());
        LivingEntity target = findNearbyHostile(level, owner);
        if (target != null) {
            targetEntityId = target.getId();
            damageDealt = false;
            setPhase(PHASE_FLYING);
        }
    }

    private void tickFlying(ServerLevel level, ServerPlayer owner) {
        Entity targetEntity = level.getEntity(targetEntityId);
        if (!(targetEntity instanceof LivingEntity target) || !target.isAlive()) {
            setPhase(PHASE_RETURNING);
            return;
        }

        Vec3 dest = target.getBoundingBox().getCenter();
        Vec3 cur = this.position();
        Vec3 dir = dest.subtract(cur);
        double dist = dir.length();

        this.setYRot(yawToward(cur, dest));

        if (dist < Math.sqrt(CLOSE_THRESHOLD_SQ)) {
            setPhase(PHASE_ATTACKING);
            return;
        }
        this.xo = cur.x;
        this.yo = cur.y;
        this.zo = cur.z;
        Vec3 move = dir.normalize().scale(Math.min(FLY_SPEED, dist));
        this.lerpPositionAndRotationStep(2, cur.x + move.x, cur.y + move.y, cur.z + move.z, entityData.get(DATA_FACING_YAW), owner.getXRot());
//        setPos(cur.x + move.x, cur.y + move.y, cur.z + move.z);
    }

    private void tickAttacking(ServerLevel level, ServerPlayer owner) {
        Entity peekTarget = level.getEntity(targetEntityId);
        if (peekTarget instanceof LivingEntity) {
            this.setYRot(yawToward(this.position(), ((LivingEntity) peekTarget).getBoundingBox().getCenter()));
        }

        if (!damageDealt && phaseAge >= DAMAGE_TICK) {
            Entity targetEntity = level.getEntity(targetEntityId);
            if (targetEntity instanceof LivingEntity target && target.isAlive()) {
                SpellDamageSource src = SpellDamageSource.source(owner, MagicDamageType.BLUNT, ModElements.EARTH.get())
                    .withSpellType(DRONE_ID);
                DamageSources.applyDamage(target, ATTACK_DAMAGE, src);
                target.knockback(2.0, this.getX() - target.getX(), this.getZ() - target.getZ());
            }
            damageDealt = true;
        }

        if (phaseAge >= ATTACK_ANIM_TICKS) {
            attackCooldown = ATTACK_COOLDOWN;
            setPhase(PHASE_RETURNING);
        }
    }

    private void tickReturning(ServerPlayer owner) {
        Vec3 hover = hoverPos(owner);
        Vec3 cur = this.position();
        Vec3 dir = hover.subtract(cur);
        double dist = dir.length();

        this.setYRot(owner.getYRot());

        if (dist < 0.3) {
            setPhase(PHASE_HOVERING);
            return;
        }

        Vec3 move = dir.normalize().scale(Math.min(FLY_SPEED, dist));
        setPos(cur.x + move.x, cur.y + move.y, cur.z + move.z);
    }

    private LivingEntity findNearbyHostile(ServerLevel level, ServerPlayer owner) {
        AABB box = owner.getBoundingBox().inflate(ATTACK_RANGE_PLAYER);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, box,
            e -> e != owner && e.isAlive() && e.getType().getCategory() == MobCategory.MONSTER);

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (LivingEntity e : candidates) {
            double dist = owner.distanceToSqr(e);
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
        double leftX = -Math.cos(yawRad);
        double leftZ = -Math.sin(yawRad);
        return player.position()
            .add(leftX * 0.7, player.getBbHeight() + 0.5, leftZ * 0.7);
    }

    private Vec3 hoverPosFromOld(LivingEntity player) {
        float yawRad = (float) Math.toRadians(player.getYRot());
        double leftX = -Math.cos(yawRad);
        double leftZ = -Math.sin(yawRad);
        return new Vec3(player.xo, player.yo, player.zo)
            .add(leftX * 0.7, player.getBbHeight() + 0.5, leftZ * 0.7);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hammer_drone_ctrl", 3, state -> {
            int phase = getPhase();
            if (phase == PHASE_ATTACKING) {
                state.setAnimation(RawAnimation.begin().thenPlay("attack"));
                return PlayState.CONTINUE;
            }
            if (phase == PHASE_FLYING) {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
                return PlayState.CONTINUE;
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
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
