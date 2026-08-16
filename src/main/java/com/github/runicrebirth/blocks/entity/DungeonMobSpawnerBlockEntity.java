package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.dungeon.DifficultyScaler;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonModifier;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonMobSpawnerBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { IDLE, INITIATING, SPAWNING, ENDING }

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_INITIATING = RawAnimation.begin().thenPlay("initiate_spawning").thenLoop("hold_spawning");
    private static final RawAnimation ANIM_SPAWNING = RawAnimation.begin().thenLoop("hold_spawning");
    private static final RawAnimation ANIM_ENDING = RawAnimation.begin().thenPlay("end_spawning").thenLoop("idle");

    private static final int INITIATE_TICKS = 20;
    private static final int ENDING_TICKS = 40;
    private static final int SPAWN_INTERVAL_TICKS = 10;

    // Config
    private ResourceLocation mobTypeId = ResourceLocation.withDefaultNamespace("zombie");
    private int spawnCount = 1;
    private float spawnRadius = 0.5f;
    private double activationRadius = 10.0;

    // Runtime state
    private AnimState animState = AnimState.IDLE;
    private int stateTicks = 0;
    private int spawnedCount = 0;
    private boolean hasFinishedSpawning = false;
    private final List<UUID> spawnedMobs = new ArrayList<>();

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DungeonMobSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_MOB_SPAWNER.get(), pos, state);
    }

    // --- Config accessors ---
    public ResourceLocation getMobTypeId() { return mobTypeId; }
    public int getSpawnCount() { return spawnCount; }
    public float getSpawnRadius() { return spawnRadius; }
    public double getActivationRadius() { return activationRadius; }
    public AnimState getAnimState() { return animState; }

    public void configure(ResourceLocation mobType, int count, float radius, double activation) {
        this.mobTypeId = mobType;
        this.spawnCount = count;
        this.spawnRadius = radius;
        this.activationRadius = activation;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    // --- Tracker API ---
    public int getAliveSpawnedMobCount() { return spawnedMobs.size(); }

    public boolean isSpawnComplete() {
        return hasFinishedSpawning && spawnedMobs.isEmpty();
    }

    // --- Tick ---
    public static void serverTick(Level level, BlockPos pos, BlockState state, DungeonMobSpawnerBlockEntity be) {
        be.tick((ServerLevel) level);
    }

    private void tick(ServerLevel server) {
        stateTicks++;

        spawnedMobs.removeIf(uuid -> {
            var entity = server.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        switch (animState) {
            case IDLE -> {
                Player nearest = server.getNearestPlayer(
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    activationRadius, false);
                if (nearest != null) transitionTo(AnimState.INITIATING);
            }
            case INITIATING -> {
                if (stateTicks >= INITIATE_TICKS) transitionTo(AnimState.SPAWNING);
            }
            case SPAWNING -> {
                if (spawnedCount < spawnCount && stateTicks % SPAWN_INTERVAL_TICKS == 0) {
                    spawnMob(server);
                }
                if (spawnedCount >= spawnCount) {
                    hasFinishedSpawning = true;
                    transitionTo(AnimState.ENDING);
                }
            }
            case ENDING -> {
                if (stateTicks >= ENDING_TICKS) transitionTo(AnimState.IDLE);
            }
        }
    }

    private void transitionTo(AnimState newState) {
        animState = newState;
        stateTicks = 0;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void spawnMob(ServerLevel server) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(mobTypeId).orElse(null);
        if (type == null) return;

        double angle = server.random.nextDouble() * 2 * Math.PI;
        double dist = server.random.nextDouble() * spawnRadius;

        var entity = type.spawn(server, worldPosition.above(), MobSpawnType.SPAWNER);
        if (entity != null) {
            entity.setPos(
                worldPosition.getX() + 0.5 + Math.cos(angle) * dist,
                worldPosition.getY() + 1,
                worldPosition.getZ() + 0.5 + Math.sin(angle) * dist
            );
            if (entity instanceof LivingEntity living) {
                DungeonInstance inst = getDungeonInstance(server);
                DifficultyScaler.applyDifficulty(living, inst != null ? inst.getDifficulty() : 1);
                if (inst != null) inst.getModifiers().forEach(m -> m.applyToMob(living));
                spawnedMobs.add(living.getUUID());
            }
            spawnedCount++;
        }
    }

    private DungeonInstance getDungeonInstance(ServerLevel server) {
        Player nearest = server.getNearestPlayer(
            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, false);
        return nearest != null ? DungeonInstanceManager.get().getInstanceForPlayer(nearest.getUUID()) : null;
    }

    // --- GeckoLib ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> switch (animState) {
            case IDLE -> state.setAndContinue(ANIM_IDLE);
            case INITIATING -> state.setAndContinue(ANIM_INITIATING);
            case SPAWNING -> state.setAndContinue(ANIM_SPAWNING);
            case ENDING -> state.setAndContinue(ANIM_ENDING);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // --- NBT ---
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("mob_type", mobTypeId.toString());
        tag.putInt("spawn_count", spawnCount);
        tag.putFloat("spawn_radius", spawnRadius);
        tag.putDouble("activation_radius", activationRadius);
        tag.putInt("anim_state", animState.ordinal());
        tag.putInt("state_ticks", stateTicks);
        tag.putInt("spawned_count", spawnedCount);
        tag.putBoolean("finished_spawning", hasFinishedSpawning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("mob_type")) mobTypeId = ResourceLocation.parse(tag.getString("mob_type"));
        if (tag.contains("spawn_count")) spawnCount = tag.getInt("spawn_count");
        if (tag.contains("spawn_radius")) spawnRadius = tag.getFloat("spawn_radius");
        if (tag.contains("activation_radius")) activationRadius = tag.getDouble("activation_radius");
        if (tag.contains("anim_state")) {
            int ord = tag.getInt("anim_state");
            animState = ord < AnimState.values().length ? AnimState.values()[ord] : AnimState.IDLE;
        }
        if (tag.contains("state_ticks")) stateTicks = tag.getInt("state_ticks");
        if (tag.contains("spawned_count")) spawnedCount = tag.getInt("spawned_count");
        if (tag.contains("finished_spawning")) hasFinishedSpawning = tag.getBoolean("finished_spawning");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("anim_state", animState.ordinal());
        tag.putFloat("spawn_radius", spawnRadius);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
