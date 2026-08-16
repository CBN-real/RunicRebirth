package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoomTrackerBlockEntity extends BlockEntity {

    public enum State { INACTIVE, COUNTDOWN, ACTIVE, COMPLETE }

    private static final int COUNTDOWN_TICKS = 100; // 5 seconds

    // Config
    private BlockPos corner1 = BlockPos.ZERO;
    private BlockPos corner2 = BlockPos.ZERO;
    private final List<BlockPos> spawnerPositions = new ArrayList<>();
    private final List<BlockPos> doorPositions = new ArrayList<>();
    private final List<BlockPos> activationBlockPositions = new ArrayList<>();
    private int timeBonusSeconds = 60;

    // Runtime
    private State state = State.INACTIVE;
    private int stateTicks = 0;
    @Nullable
    private ServerBossEvent bossBar;

    public DungeonRoomTrackerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_ROOM_TRACKER.get(), pos, state);
    }

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
                if (!getPlayersInZone(server).isEmpty()) {
                    state = State.COUNTDOWN;
                    stateTicks = 0;
                    setChanged();
                }
            }
            case COUNTDOWN -> {
                stateTicks++;
                if (stateTicks >= COUNTDOWN_TICKS) {
                    state = State.ACTIVE;
                    stateTicks = 0;
                    setChanged();
                    activateRoom(server);
                }
            }
            case ACTIVE -> {
                if (bossBar == null) recreateBossBar(server);
                updateBossBar(server);
                if (isRoomComplete(server)) {
                    state = State.COMPLETE;
                    setChanged();
                    onRoomComplete(server);
                }
            }
            case COMPLETE -> {}
        }
    }

    private void activateRoom(ServerLevel server) {
        // Close all dungeon doors
        for (BlockPos doorPos : doorPositions) {
            BlockState doorState = server.getBlockState(doorPos);
            if (doorState.getBlock() instanceof DungeonDoorBlock) {
                DungeonDoorBlock.setOpen(server, doorPos, doorState, false);
            }
        }
        recreateBossBar(server);
    }

    private void recreateBossBar(ServerLevel server) {
        int total = getTotalMobCount(server);
        bossBar = new ServerBossEvent(
            Component.literal("Enemies Remaining: " + total + "/" + total),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
        );
        bossBar.setProgress(total > 0 ? 1.0f : 0.0f);
        for (ServerPlayer player : getPlayersInZone(server)) {
            bossBar.addPlayer(player);
        }
    }

    private void updateBossBar(ServerLevel server) {
        if (bossBar == null) return;

        int total = getTotalMobCount(server);
        int alive = getAliveMobCount(server);
        bossBar.setName(Component.literal("Enemies Remaining: " + alive + "/" + total));
        bossBar.setProgress(total > 0 ? (float) alive / total : 0f);

        List<ServerPlayer> playersInZone = getPlayersInZone(server);
        // Add newcomers
        for (ServerPlayer player : playersInZone) bossBar.addPlayer(player);
        // Remove leavers
        new ArrayList<>(bossBar.getPlayers()).forEach(player -> {
            if (!playersInZone.contains(player)) bossBar.removePlayer(player);
        });
    }

    private boolean isRoomComplete(ServerLevel server) {
        if (!allMobsDead(server)) return false;
        for (BlockPos activationPos : activationBlockPositions) {
            if (!server.hasNeighborSignal(activationPos)) return false;
        }
        return true;
    }

    private boolean allMobsDead(ServerLevel server) {
        // If no spawners configured, mobs are trivially "done"
        if (spawnerPositions.isEmpty()) return true;
        for (BlockPos spawnerPos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(spawnerPos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) {
                if (!spawner.isSpawnComplete()) return false;
            }
        }
        return true;
    }

    private void onRoomComplete(ServerLevel server) {
        List<ServerPlayer> playersInZone = getPlayersInZone(server);

        // Show title
        for (ServerPlayer player : playersInZone) {
            player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
            player.connection.send(new ClientboundSetTitleTextPacket(
                Component.literal("Room Cleared!").withStyle(ChatFormatting.GOLD)));
            player.connection.send(new ClientboundSetSubtitleTextPacket(
                Component.literal("+" + timeBonusSeconds + "s time bonus").withStyle(ChatFormatting.GREEN)));
        }

        // Add time to dungeon timer
        for (ServerPlayer player : playersInZone) {
            DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPlayer(player.getUUID());
            if (inst != null) inst.addTime(timeBonusSeconds * 20);
        }

        // Open doors
        for (BlockPos doorPos : doorPositions) {
            BlockState doorState = server.getBlockState(doorPos);
            if (doorState.getBlock() instanceof DungeonDoorBlock) {
                DungeonDoorBlock.setOpen(server, doorPos, doorState, true);
            }
        }

        // Clear boss bar
        if (bossBar != null) {
            bossBar.removeAllPlayers();
            bossBar = null;
        }
    }

    private List<ServerPlayer> getPlayersInZone(ServerLevel server) {
        if (corner1.equals(BlockPos.ZERO) && corner2.equals(BlockPos.ZERO)) return List.of();
        AABB zone = AABB.encapsulatingFullBlocks(corner1, corner2);
        return server.getPlayers(player -> zone.intersects(player.getBoundingBox()));
    }

    private int getTotalMobCount(ServerLevel server) {
        int total = 0;
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) total += spawner.getSpawnCount();
        }
        return total;
    }

    private int getAliveMobCount(ServerLevel server) {
        int alive = 0;
        for (BlockPos pos : spawnerPositions) {
            BlockEntity be = server.getBlockEntity(pos);
            if (be instanceof DungeonMobSpawnerBlockEntity spawner) alive += spawner.getAliveSpawnedMobCount();
        }
        return alive;
    }

    // --- Config serialization (for screen sync) ---
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
        CompoundTag config = serializeConfig();
        tag.merge(config);
        tag.putInt("tracker_state", state.ordinal());
        tag.putInt("state_ticks", stateTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        deserializeConfig(tag);
        if (tag.contains("tracker_state")) {
            int ord = tag.getInt("tracker_state");
            state = ord < State.values().length ? State.values()[ord] : State.INACTIVE;
        }
        if (tag.contains("state_ticks")) stateTicks = tag.getInt("state_ticks");
    }
}
