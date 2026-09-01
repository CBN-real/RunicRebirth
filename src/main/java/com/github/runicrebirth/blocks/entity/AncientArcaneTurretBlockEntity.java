package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.AncientArcaneTurretBlock;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.entities.spells.MagicProjectileEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.List;

public class AncientArcaneTurretBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { IDLE, ACTIVATING, DEACTIVATING }

    private static final RawAnimation ANIM_IDLE        = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_ACTIVATING  = RawAnimation.begin().thenPlay("initiate_activated").thenLoop("hold_activated");
    private static final RawAnimation ANIM_DEACTIVATING = RawAnimation.begin().thenPlay("end_activated").thenLoop("idle");

    private static final int ATTACK_COOLDOWN = 100;
    private static final double DETECTION_RANGE = 12.0;
    private static final float PROJECTILE_DAMAGE = 6.0f;
    private static final float PROJECTILE_SPEED = 0.3f;
    private static final float MAX_HEALTH = 10.0f; // 5 hearts
    private static final int REVIVE_TICKS = 60 * 20; // 60 seconds
    private static final int CRACKLING_RESPAWN_INTERVAL = 40;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private AnimState animState = AnimState.IDLE;
    private boolean playerInRange = false;
    private int cooldown = ATTACK_COOLDOWN;
    private float health = MAX_HEALTH;
    private boolean dead = false;
    private int reviveTimer = 0;
    private int cracklingRespawnTimer = 0;

    public AncientArcaneTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_ARCANE_TURRET.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  AncientArcaneTurretBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void damage(float amount) {
        if (dead) return;
        health -= amount;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        if (health <= 0) {
            health = 0;
            dead = true;
            reviveTimer = REVIVE_TICKS;
            cracklingRespawnTimer = 0;
            playerInRange = false;
            setAnimState(AnimState.IDLE);
            spawnDeathCrackling();
        }
    }

    public boolean isDead() { return dead; }

    private void spawnDeathCrackling() {
        if (level == null || level.isClientSide()) return;
        EnergyCracklingEntity crackling = new EnergyCracklingEntity(
                level, 0.5f, 0x4488FF, CRACKLING_RESPAWN_INTERVAL + 5, 1.0f, 1.0f, 0.7f);
        Vec3 center = Vec3.atCenterOf(worldPosition);
        crackling.setPos(center.x, center.y, center.z);
        level.addFreshEntity(crackling);
    }

    private void tickDead(Level level) {
        if (--cracklingRespawnTimer <= 0) {
            cracklingRespawnTimer = CRACKLING_RESPAWN_INTERVAL;
            spawnDeathCrackling();
        }
        if (--reviveTimer <= 0) {
            dead = false;
            health = MAX_HEALTH;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (dead) {
            tickDead(level);
            return;
        }

        AABB searchBox = AABB.ofSize(Vec3.atCenterOf(pos),
                DETECTION_RANGE * 2, DETECTION_RANGE * 2, DETECTION_RANGE * 2);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox,
                p -> !p.isSpectator() && !p.isCreative()
                        && p.distanceToSqr(Vec3.atCenterOf(pos)) <= DETECTION_RANGE * DETECTION_RANGE);

        boolean wasInRange = playerInRange;
        playerInRange = !players.isEmpty();

        if (!wasInRange && playerInRange) {
            setAnimState(AnimState.ACTIVATING);
            level.playSound(null, pos, ModSounds.DUNGEON_TURRET_POWER_ON.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        } else if (wasInRange && !playerInRange) {
            setAnimState(AnimState.DEACTIVATING);
        }

        if (!playerInRange) return;

        if (--cooldown > 0) return;
        cooldown = ATTACK_COOLDOWN;

        Player target = players.stream()
                .min((a, b) -> Double.compare(
                        a.distanceToSqr(Vec3.atCenterOf(pos)),
                        b.distanceToSqr(Vec3.atCenterOf(pos))))
                .orElse(null);
        if (target == null) return;

        net.minecraft.core.Direction fireDir = AncientArcaneTurretBlock.getFireDirection(state);
        Vec3 nozzle = Vec3.atCenterOf(pos).add(
                fireDir.getStepX() * 0.35,
                fireDir.getStepY() * 0.35,
                fireDir.getStepZ() * 0.35);
        Vec3 toTarget = target.getEyePosition().subtract(nozzle).normalize();

        MagicProjectileEntity projectile = new MagicProjectileEntity(
                level, nozzle, toTarget, PROJECTILE_SPEED, PROJECTILE_DAMAGE);
        projectile.setTrackingTarget(target);
        level.addFreshEntity(projectile);
    }

    private void setAnimState(AnimState newState) {
        if (this.animState != newState) {
            this.animState = newState;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<AncientArcaneTurretBlockEntity>("controller", 0, state ->
            switch (animState) {
                case ACTIVATING   -> state.setAndContinue(ANIM_ACTIVATING);
                case DEACTIVATING -> state.setAndContinue(ANIM_DEACTIVATING);
                default           -> state.setAndContinue(ANIM_IDLE);
            }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("animState", animState.name());
        output.putBoolean("playerInRange", playerInRange);
        output.putFloat("health", health);
        output.putBoolean("dead", dead);
        output.putInt("reviveTimer", reviveTimer);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        try {
            animState = AnimState.valueOf(input.getStringOr("animState", "IDLE"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.IDLE;
        }
        playerInRange = input.getBooleanOr("playerInRange", false);
        health = input.getFloatOr("health", MAX_HEALTH);
        dead = input.getBooleanOr("dead", false);
        reviveTimer = input.getIntOr("reviveTimer", 0);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
