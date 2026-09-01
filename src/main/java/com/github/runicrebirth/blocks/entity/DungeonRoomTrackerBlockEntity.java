package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonRoomType;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoomTrackerBlockEntity extends BlockEntity {

    public enum State { INACTIVE, COUNTDOWN, WAVE_ACTIVE, BETWEEN_WAVES, COMPLETE }

    private static final int COUNTDOWN_TICKS    = 100; // 5 seconds
    private static final int BETWEEN_WAVE_TICKS = 200; // 10 seconds

    // Room type
    private DungeonRoomType roomType = DungeonRoomType.NORMAL;

    // Config
    private BlockPos corner1 = BlockPos.ZERO;
    private BlockPos corner2 = BlockPos.ZERO;
    private final List<BlockPos> spawnerPositions           = new ArrayList<>();
    private final List<BlockPos> doorPositions              = new ArrayList<>();
    private final List<BlockPos> activationBlockPositions   = new ArrayList<>();
    private int timeBonusSeconds = 60;

    // Runtime
    private State state           = State.INACTIVE;
    private int stateTicks        = 0;
    private int currentWave       = 0;
    private int totalWaves        = 0;
    private int betweenWaveTicks  = 0;
    private int currentWaveTotalMobs = 0;
    @Nullable
    private ServerBossEvent bossBar;

    public DungeonRoomTrackerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_ROOM_TRACKER.get(), pos, state);
    }

    // --- Room type getters/setters ---
    public DungeonRoomType getRoomType() { return roomType; }
    public void setRoomType(DungeonRoomType type) { this.roomType = type; setChanged(); }
    public boolean isBossRoom() { return roomType == DungeonRoomType.BOSS; }

    // --- Getters ---
    public BlockPos getCorner1() { return corner1; }
    public BlockPos getCorner2() { return corner2; }
    public List<BlockPos> getSpawnerPositions() { return spawnerPositions; }
    public List<BlockPos> getDoorPositions() { return doorPositions; }
    public List<BlockPos> getActivationBlockPositions() { return activationBlockPositions; }
    public int getTimeBonusSeconds() { return timeBonusSeconds; }
    public State getTrackerState() { return state; }

    // --- Config setters ---
    public void setCorners(BlockPos c1, BlockPos c2) { corner1 = c1; corner2 = c2; setChanged(); }
    public void setTimeBonusSeconds(int seconds) { timeBonusSeconds = seconds; setChanged(); }

    public void setConfig(BlockPos c1, BlockPos c2,
                          List<BlockPos> spawners, List<BlockPos> doors,
                          List<BlockPos> activationBlocks, int timeBonusSecs) {
        this.corner1 = c1;
        this.corner2 = c2;
        this.spawnerPositions.clear();
        this.spawnerPositions.addAll(spawners);
        this.doorPositions.clear();
        this.doorPositions.addAll(doors);
        this.activationBlockPositions.clear();
        this.activationBlockPositions.addAll(activationBlocks);
        this.timeBonusSeconds = timeBonusSecs;
        setChanged();
    }

    // --- Tick ---
    public static void serverTick(Level level, BlockPos pos, BlockState state, DungeonRoomTrackerBlockEntity be) {
        be.tick((ServerLevel) level);
    }

    private void tick(ServerLevel server) {
        switch (state) {
            case INACTIVE -> {
                List<ServerPlayer> inZone = getPlayersInZone(server);
                if (!inZone.isEmpty() && allDungeonPlayersInZone(server, inZone)) {
                    state = State.COUNTDOWN;
                    stateTicks = 0;
                    setChanged();
                }
            }
            case COUNTDOWN -> {
                stateTicks++;
                if (stateTicks >= COUNTDOWN_TICKS) activateRoom(server);
            }
            case WAVE_ACTIVE -> {
                if (bossBar == null) recreateBossBar(server);
                if (totalWaves == 0) {
                    // No spawners configured — only activation blocks matter
                    if (activationBlocksDone(server)) {
                        state = State.COMPLETE;
                        setChanged();
                        onRoomComplete(server);
                    } else {
                        updateBossBarActivation(server);
                    }
                } else if (isCurrentWaveComplete(server)) {
                    if (currentWave >= totalWaves) {
                        if (activationBlocksDone(server)) {
                            state = State.COMPLETE;
                            setChanged();
                            onRoomComplete(server);
                        } else {
                            updateBossBarActivation(server);
                        }
                    } else {
                        state = State.BETWEEN_WAVES;
                        betweenWaveTicks = BETWEEN_WAVE_TICKS;
                        setChanged();
                        updateBossBarBetweenWaves(server);
                    }
                } else {
                    updateBossBarWave(server);
                }
            }
            case BETWEEN_WAVES -> {
                betweenWaveTicks--;
                updateBossBarBetweenWaves(server);
                if (betweenWaveTicks <= 0) {
                    currentWave++;
                    startWave(server, currentWave);
                    state = State.WAVE_ACTIVE;
                    setChanged();
                }
            }
            case COMPLETE -> {}
        }
    }

    private void activateRoom(ServerLevel server) {
        totalWaves = computeTotalWaves(server);
        currentWave = 1;
        state = State.WAVE_ACTIVE;
        stateTicks = 0;
        if (totalWaves > 0) startWave(server, currentWave);
        setChanged();
        recreateBossBar(server);
    }

    private void startWave(ServerLevel server, int wave) {
        float multiplier = getSpawnerCountMultiplier(server);
        currentWaveTotalMobs = 0;
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) {
                currentWaveTotalMobs += spawner.getWaveMobCount(wave, multiplier);
                spawner.startWave(wave, multiplier);
            }
        }
        setChanged();
    }

    public void reset(ServerLevel server) {
        for (BlockPos doorPos : doorPositions) {
            BlockState doorState = server.getBlockState(doorPos);
            if (doorState.getBlock() instanceof DungeonDoorBlock)
                DungeonDoorBlock.setOpen(server, doorPos, doorState, true);
        }
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) spawner.resetWave();
        }
        if (bossBar != null) { bossBar.removeAllPlayers(); bossBar = null; }
        state = State.INACTIVE;
        stateTicks = 0;
        currentWave = 0;
        totalWaves = 0;
        betweenWaveTicks = 0;
        currentWaveTotalMobs = 0;
        setChanged();
    }

    private int computeTotalWaves(ServerLevel server) {
        int max = 0;
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner)
                max = Math.max(max, spawner.getMaxWave());
        }
        return max;
    }

    private float getSpawnerCountMultiplier(ServerLevel server) {
        List<ServerPlayer> players = getPlayersInZone(server);
        if (players.isEmpty()) return 1.0f;
        DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPlayer(players.get(0).getUUID());
        return inst != null ? inst.getSpawnerCountMultiplier() : 1.0f;
    }

    private boolean isCurrentWaveComplete(ServerLevel server) {
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner)
                if (!spawner.isSpawnComplete()) return false;
        }
        return true;
    }

    private boolean activationBlocksDone(ServerLevel server) {
        for (BlockPos activationPos : activationBlockPositions)
            if (!server.hasNeighborSignal(activationPos)) return false;
        return true;
    }

    private void recreateBossBar(ServerLevel server) {
        if (bossBar != null) bossBar.removeAllPlayers();
        String name = totalWaves > 0
            ? "Wave " + currentWave + "/" + totalWaves
            : "Room Active";
        bossBar = new ServerBossEvent(
            Component.literal(name),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
        );
        bossBar.setProgress(1.0f);
        for (ServerPlayer player : getPlayersInZone(server)) bossBar.addPlayer(player);
    }

    private void updateBossBarWave(ServerLevel server) {
        if (bossBar == null) return;
        int alive = getAliveMobCount(server);
        bossBar.setName(Component.literal(
            "Wave " + currentWave + "/" + totalWaves + " — " + alive + " enemies remaining"));
        bossBar.setProgress(currentWaveTotalMobs > 0 ? (float) alive / currentWaveTotalMobs : 0f);
        syncBossBarPlayers(server);
    }

    private void updateBossBarBetweenWaves(ServerLevel server) {
        if (bossBar == null) return;
        int secsLeft = Math.max(0, (betweenWaveTicks + 19) / 20);
        int nextWave = currentWave + 1;
        bossBar.setName(Component.literal(
            "Wave " + nextWave + " in " + secsLeft + "s — " + totalWaves + " total waves"));
        bossBar.setProgress((float) currentWave / Math.max(1, totalWaves));
        syncBossBarPlayers(server);
    }

    private void updateBossBarActivation(ServerLevel server) {
        if (bossBar == null) return;
        bossBar.setName(Component.literal("Activate the mechanisms to complete the room"));
        bossBar.setProgress(0f);
        syncBossBarPlayers(server);
    }

    private void syncBossBarPlayers(ServerLevel server) {
        if (bossBar == null) return;
        List<ServerPlayer> playersInZone = getPlayersInZone(server);
        for (ServerPlayer player : playersInZone) bossBar.addPlayer(player);
        new ArrayList<>(bossBar.getPlayers()).forEach(player -> {
            if (!playersInZone.contains(player)) bossBar.removePlayer(player);
        });
    }

    private void onRoomComplete(ServerLevel server) {
        List<ServerPlayer> playersInZone = getPlayersInZone(server);

        if (isBossRoom()) {
            for (ServerPlayer player : playersInZone) {
                player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                player.connection.send(new ClientboundSetTitleTextPacket(
                    Component.literal("Dungeon Complete!").withStyle(ChatFormatting.GOLD)));
                player.connection.send(new ClientboundSetSubtitleTextPacket(
                    Component.literal("Victory!").withStyle(ChatFormatting.GREEN)));
            }
            DungeonInstance inst = getInstanceFromPlayers(server);
            if (inst != null) {
                DungeonInstanceManager.get().completeInstance(inst.getInstanceId(), server.getServer());
            }
        } else {
            for (ServerPlayer player : playersInZone) {
                player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
                player.connection.send(new ClientboundSetTitleTextPacket(
                    Component.literal("Room Cleared!").withStyle(ChatFormatting.GOLD)));
                player.connection.send(new ClientboundSetSubtitleTextPacket(
                    Component.literal("+" + timeBonusSeconds + "s time bonus").withStyle(ChatFormatting.GREEN)));
            }
            for (ServerPlayer player : playersInZone) {
                DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPlayer(player.getUUID());
                if (inst != null) inst.addTime(timeBonusSeconds * 20);
            }
        }

        for (BlockPos doorPos : doorPositions) {
            BlockState doorState = server.getBlockState(doorPos);
            if (doorState.getBlock() instanceof DungeonDoorBlock)
                DungeonDoorBlock.setOpen(server, doorPos, doorState, true);
        }
        if (bossBar != null) { bossBar.removeAllPlayers(); bossBar = null; }
    }

    @Nullable
    private DungeonInstance getInstanceFromPlayers(ServerLevel server) {
        for (ServerPlayer player : getPlayersInZone(server)) {
            DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPlayer(player.getUUID());
            if (inst != null) return inst;
        }
        return null;
    }

    private List<ServerPlayer> getPlayersInZone(ServerLevel server) {
        if (corner1.equals(BlockPos.ZERO) && corner2.equals(BlockPos.ZERO)) return List.of();
        AABB zone = AABB.encapsulatingFullBlocks(corner1, corner2);
        return server.getPlayers(player -> zone.intersects(player.getBoundingBox()));
    }

    private boolean allDungeonPlayersInZone(ServerLevel server, List<ServerPlayer> inZone) {
        if (inZone.isEmpty()) return false;
        DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPlayer(inZone.get(0).getUUID());
        if (inst == null) return true;
        java.util.Set<java.util.UUID> zoneIds = new java.util.HashSet<>();
        for (ServerPlayer p : inZone) zoneIds.add(p.getUUID());
        for (java.util.UUID id : inst.getPlayers()) {
            if (!zoneIds.contains(id)) return false;
        }
        return true;
    }

    private int getAliveMobCount(ServerLevel server) {
        int alive = 0;
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) alive += spawner.getAliveSpawnedMobCount();
        }
        return alive;
    }

    // --- Config serialization ---
    public CompoundTag serializeConfig() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("corner1", corner1.asLong());
        tag.putLong("corner2", corner2.asLong());
        tag.putLongArray("spawners", spawnerPositions.stream().mapToLong(BlockPos::asLong).toArray());
        tag.putLongArray("doors", doorPositions.stream().mapToLong(BlockPos::asLong).toArray());
        tag.putLongArray("activation_blocks", activationBlockPositions.stream().mapToLong(BlockPos::asLong).toArray());
        tag.putInt("time_bonus", timeBonusSeconds);
        return tag;
    }

    public void deserializeConfig(CompoundTag tag) {
        if (tag.contains("corner1")) corner1 = BlockPos.of(tag.getLong("corner1"));
        if (tag.contains("corner2")) corner2 = BlockPos.of(tag.getLong("corner2"));
        spawnerPositions.clear();
        if (tag.contains("spawners"))
            for (long l : tag.getLongArray("spawners")) spawnerPositions.add(BlockPos.of(l));
        doorPositions.clear();
        if (tag.contains("doors"))
            for (long l : tag.getLongArray("doors")) doorPositions.add(BlockPos.of(l));
        activationBlockPositions.clear();
        if (tag.contains("activation_blocks"))
            for (long l : tag.getLongArray("activation_blocks")) activationBlockPositions.add(BlockPos.of(l));
        if (tag.contains("time_bonus")) timeBonusSeconds = tag.getInt("time_bonus");
        setChanged();
    }

    // --- NBT ---
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.merge(serializeConfig());
        tag.putInt("room_type", roomType.ordinal());
        tag.putInt("tracker_state", state.ordinal());
        tag.putInt("state_ticks", stateTicks);
        tag.putInt("current_wave", currentWave);
        tag.putInt("total_waves", totalWaves);
        tag.putInt("between_wave_ticks", betweenWaveTicks);
        tag.putInt("wave_total_mobs", currentWaveTotalMobs);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        deserializeConfig(tag);
        if (tag.contains("room_type")) {
            int ord = tag.getInt("room_type");
            roomType = ord < DungeonRoomType.values().length ? DungeonRoomType.values()[ord] : DungeonRoomType.NORMAL;
        }
        if (tag.contains("tracker_state")) {
            int ord = tag.getInt("tracker_state");
            state = ord < State.values().length ? State.values()[ord] : State.INACTIVE;
        }
        if (tag.contains("state_ticks"))       stateTicks           = tag.getInt("state_ticks");
        if (tag.contains("current_wave"))       currentWave          = tag.getInt("current_wave");
        if (tag.contains("total_waves"))        totalWaves           = tag.getInt("total_waves");
        if (tag.contains("between_wave_ticks")) betweenWaveTicks     = tag.getInt("between_wave_ticks");
        if (tag.contains("wave_total_mobs"))    currentWaveTotalMobs = tag.getInt("wave_total_mobs");
        // Safety: reset if mid-wave state loaded but no wave data (e.g. old save file)
        if ((state == State.WAVE_ACTIVE || state == State.BETWEEN_WAVES) && totalWaves == 0 && spawnerPositions.isEmpty())
            state = State.INACTIVE;
    }
}
