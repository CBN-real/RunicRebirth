package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.dungeon.DifficultyScaler;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonModifier;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrialSpawnerBlockEntity extends BlockEntity {

    private ResourceLocation mobTypeId = ResourceLocation.withDefaultNamespace("zombie");
    private int mobCount = 4;
    private int waveCount = 1;
    private int waveDelay = 100;
    private double activationRadius = 8.0;

    private int currentWave = 0;
    private int spawnedThisWave = 0;
    private int waveDelayTick = 0;
    private boolean activated = false;
    private boolean completed = false;
    private final List<UUID> spawnedMobs = new ArrayList<>();

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_SPAWNER.get(), pos, state);
    }

    public void configure(ResourceLocation mobType, int count, int waves, int delay) {
        this.mobTypeId = mobType;
        this.mobCount = count;
        this.waveCount = waves;
        this.waveDelay = delay;
    }

    public boolean isCompleted() { return completed; }

    public void serverTick() {
        if (completed || !(this.level instanceof ServerLevel server)) return;

        if (!activated) {
            Player nearest = server.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), activationRadius, false);
            if (nearest != null) {
                activated = true;
            }
            return;
        }

        // Clean up dead mobs from tracking
        spawnedMobs.removeIf(uuid -> {
            var entity = server.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        if (currentWave < waveCount) {
            if (spawnedThisWave < mobCount) {
                if (waveDelayTick <= 0) {
                    spawnMob(server);
                    spawnedThisWave++;
                }
            } else if (spawnedMobs.isEmpty()) {
                currentWave++;
                spawnedThisWave = 0;
                waveDelayTick = waveDelay;
            }
        } else if (spawnedMobs.isEmpty()) {
            completed = true;
        }

        if (waveDelayTick > 0) waveDelayTick--;
    }

    private void spawnMob(ServerLevel level) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(mobTypeId);
        if (type == null) return;

        var entity = type.spawn(level, worldPosition.above(2), MobSpawnType.SPAWNER);
        if (entity instanceof LivingEntity living) {
            DungeonInstance inst = getDungeonInstance();
            int difficulty = inst != null ? inst.getDifficulty() : 1;
            DifficultyScaler.applyDifficulty(living, difficulty);
            if (inst != null) {
                for (DungeonModifier mod : inst.getModifiers()) {
                    mod.applyToMob(living);
                }
            }
            spawnedMobs.add(living.getUUID());
        }
    }

    private DungeonInstance getDungeonInstance() {
        if (!(level instanceof ServerLevel server)) return null;
        Player nearest = server.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, false);
        if (nearest != null) {
            return DungeonInstanceManager.get().getInstanceForPlayer(nearest.getUUID());
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("mob_type", mobTypeId.toString());
        tag.putInt("mob_count", mobCount);
        tag.putInt("wave_count", waveCount);
        tag.putInt("wave_delay", waveDelay);
        tag.putBoolean("activated", activated);
        tag.putBoolean("completed", completed);
        tag.putInt("current_wave", currentWave);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("mob_type")) mobTypeId = ResourceLocation.parse(tag.getString("mob_type"));
        mobCount = tag.getInt("mob_count");
        waveCount = tag.getInt("wave_count");
        waveDelay = tag.getInt("wave_delay");
        activated = tag.getBoolean("activated");
        completed = tag.getBoolean("completed");
        currentWave = tag.getInt("current_wave");
    }
}
