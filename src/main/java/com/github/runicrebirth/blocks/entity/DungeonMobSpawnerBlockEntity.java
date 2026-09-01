package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.dungeon.DifficultyScaler;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.init.ModBlockEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonMobSpawnerBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements GeoBlockEntity {

    public record MobWaveEntry(Identifier mobTypeId, int waveNumber, int count) {
        public static final Codec<MobWaveEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("mob").forGetter(MobWaveEntry::mobTypeId),
            Codec.INT.fieldOf("wave").forGetter(MobWaveEntry::waveNumber),
            Codec.INT.fieldOf("count").forGetter(MobWaveEntry::count)
        ).apply(inst, MobWaveEntry::new));

        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putString("mob", mobTypeId.toString());
            tag.putInt("wave", waveNumber);
            tag.putInt("count", count);
            return tag;
        }
        public static MobWaveEntry fromNbt(CompoundTag tag) {
            return new MobWaveEntry(
                Identifier.parse(tag.getString("mob").orElse("")),
                tag.getInt("wave").orElse(0),
                tag.getInt("count").orElse(0)
            );
        }
    }

    public enum AnimState { IDLE, INITIATING, SPAWNING, ENDING }

    private static final RawAnimation ANIM_IDLE       = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_INITIATING = RawAnimation.begin().thenPlay("initiate_spawning").thenLoop("hold_spawning");
    private static final RawAnimation ANIM_SPAWNING   = RawAnimation.begin().thenLoop("hold_spawning");
    private static final RawAnimation ANIM_ENDING     = RawAnimation.begin().thenPlay("end_spawning").thenLoop("idle");

    private static final int INITIATE_TICKS       = 20;
    private static final int ENDING_TICKS         = 40;
    private static final int SPAWN_INTERVAL_TICKS = 10;
    private static final int SPAWN_LINGER_TICKS   = 40; // 2s hold after last spawn before ENDING

    // Config
    private final List<MobWaveEntry> waveEntries = new ArrayList<>();
    private float spawnRadius = 0.5f;

    // Runtime â€” reset each wave, not persisted beyond wave_active flag
    private AnimState animState = AnimState.IDLE;
    private int stateTicks = 0;
    private final List<Identifier> pendingSpawnTypes = new ArrayList<>();
    private int pendingSpawnIndex = 0;
    private int spawnLingerTicks = 0;
    private boolean waveActive = false;
    private final List<UUID> spawnedMobs = new ArrayList<>();

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DungeonMobSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_MOB_SPAWNER.get(), pos, state);
    }

    // --- Accessors ---
    public List<MobWaveEntry> getWaveEntries() { return waveEntries; }
    public float getSpawnRadius() { return spawnRadius; }
    public AnimState getAnimState() { return animState; }

    public int getMaxWave() {
        return waveEntries.stream().mapToInt(MobWaveEntry::waveNumber).max().orElse(0);
    }

    public int getWaveMobCount(int wave, float multiplier) {
        int total = 0;
        for (MobWaveEntry entry : waveEntries) {
            if (entry.waveNumber() == wave)
                total += Math.max(1, Math.round(entry.count() * multiplier));
        }
        return total;
    }

    // --- Tracker API ---
    public int getAliveSpawnedMobCount() { return spawnedMobs.size(); }

    public boolean isSpawnComplete() {
        if (!waveActive) return true;
        return pendingSpawnIndex >= pendingSpawnTypes.size() && spawnedMobs.isEmpty();
    }

    public void startWave(int wave, float countMultiplier) {
        pendingSpawnTypes.clear();
        pendingSpawnIndex = 0;
        spawnLingerTicks = 0;
        spawnedMobs.clear();

        for (MobWaveEntry entry : waveEntries) {
            if (entry.waveNumber() == wave) {
                int count = Math.max(1, Math.round(entry.count() * countMultiplier));
                for (int i = 0; i < count; i++) pendingSpawnTypes.add(entry.mobTypeId());
            }
        }

        if (!pendingSpawnTypes.isEmpty()) {
            waveActive = true;
            transitionTo(AnimState.INITIATING);
        } else {
            waveActive = false;
        }
        setChanged();
    }

    public void resetWave() {
        pendingSpawnTypes.clear();
        pendingSpawnIndex = 0;
        spawnLingerTicks = 0;
        spawnedMobs.clear();
        waveActive = false;
        animState = AnimState.IDLE;
        stateTicks = 0;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
            case IDLE -> {}
            case INITIATING -> {
                if (stateTicks >= INITIATE_TICKS) transitionTo(AnimState.SPAWNING);
            }
            case SPAWNING -> {
                if (pendingSpawnIndex < pendingSpawnTypes.size() && stateTicks % SPAWN_INTERVAL_TICKS == 0) {
                    spawnMob(server, pendingSpawnTypes.get(pendingSpawnIndex));
                    pendingSpawnIndex++;
                    spawnLingerTicks = 0;
                }
                if (pendingSpawnIndex >= pendingSpawnTypes.size()) {
                    if (++spawnLingerTicks >= SPAWN_LINGER_TICKS) transitionTo(AnimState.ENDING);
                }
            }
            case ENDING -> {
                if (stateTicks >= ENDING_TICKS) transitionTo(AnimState.IDLE);
            }
        }
    }

    private void transitionTo(AnimState next) {
        animState = next;
        stateTicks = 0;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void spawnMob(ServerLevel server, Identifier mobTypeId) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(mobTypeId).orElse(null);
        if (type == null) return;

        double angle = server.getRandom().nextDouble() * 2 * Math.PI;
        double dist  = server.getRandom().nextDouble() * spawnRadius;

        var entity = type.spawn(server, worldPosition.above(), EntitySpawnReason.SPAWNER);
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
        }
    }

    private DungeonInstance getDungeonInstance(ServerLevel server) {
        Player nearest = server.getNearestPlayer(
            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 64, false);
        return nearest != null ? DungeonInstanceManager.get().getInstanceForPlayer(nearest.getUUID()) : null;
    }

    // --- Config serialization (for screen sync) ---
    public CompoundTag serializeConfig() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (MobWaveEntry entry : waveEntries) list.add(entry.toNbt());
        tag.put("wave_entries", list);
        tag.putFloat("spawn_radius", spawnRadius);
        return tag;
    }

    public void deserializeConfig(CompoundTag tag) {
        waveEntries.clear();
        if (tag.contains("wave_entries")) {
            ListTag list = tag.getListOrEmpty("wave_entries");
            for (int i = 0; i < list.size(); i++) waveEntries.add(MobWaveEntry.fromNbt(list.getCompound(i).orElseGet(net.minecraft.nbt.CompoundTag::new)));
        }
        if (tag.contains("spawn_radius")) spawnRadius = tag.getFloat("spawn_radius").orElse(spawnRadius);
        setChanged();
    }

    // --- GeckoLib ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DungeonMobSpawnerBlockEntity>("controller", 0, state -> switch (animState) {
            case IDLE       -> state.setAndContinue(ANIM_IDLE);
            case INITIATING -> state.setAndContinue(ANIM_INITIATING);
            case SPAWNING   -> state.setAndContinue(ANIM_SPAWNING);
            case ENDING     -> state.setAndContinue(ANIM_ENDING);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // --- NBT ---
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("wave_entries", MobWaveEntry.CODEC.listOf(), waveEntries);
        output.putFloat("spawn_radius", spawnRadius);
        output.putInt("anim_state", animState.ordinal());
        output.putBoolean("wave_active", waveActive);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        waveEntries.clear();
        waveEntries.addAll(input.read("wave_entries", MobWaveEntry.CODEC.listOf()).orElse(java.util.List.of()));
        spawnRadius = input.getFloatOr("spawn_radius", 0.5f);
        int ord = input.getIntOr("anim_state", 0);
        animState = ord < AnimState.values().length ? AnimState.values()[ord] : AnimState.IDLE;
        waveActive = input.getBooleanOr("wave_active", false);
        // pendingSpawnTypes not persisted; after reload isSpawnComplete() returns true (empty list)
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
