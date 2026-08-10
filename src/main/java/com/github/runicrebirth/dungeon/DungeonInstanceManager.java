package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonInstanceManager {

    private static final int INSTANCE_SPACING = 1024;
    private static final int INSTANCE_Y = 64;

    private static DungeonInstanceManager instance;

    private final Map<UUID, DungeonInstance> instances = new HashMap<>();
    private final Map<UUID, UUID> playerToInstance = new HashMap<>();
    private final AtomicInteger nextSlot = new AtomicInteger(0);

    public static DungeonInstanceManager get() {
        if (instance == null) {
            instance = new DungeonInstanceManager();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public DungeonInstance createInstance(DungeonType type, int difficulty,
                                          BlockPos returnPos, ResourceLocation returnDimension) {
        int slot = nextSlot.getAndIncrement();
        BlockPos origin = new BlockPos(slot * INSTANCE_SPACING, INSTANCE_Y, 0);
        UUID id = UUID.randomUUID();
        DungeonInstance inst = new DungeonInstance(id, type, difficulty, origin, returnPos, returnDimension);
        instances.put(id, inst);
        return inst;
    }

    public void enterInstance(ServerPlayer player, DungeonInstance inst) {
        inst.addPlayer(player.getUUID());
        playerToInstance.put(player.getUUID(), inst.getInstanceId());
    }

    public void leaveInstance(ServerPlayer player) {
        UUID instanceId = playerToInstance.remove(player.getUUID());
        if (instanceId != null) {
            DungeonInstance inst = instances.get(instanceId);
            if (inst != null) {
                inst.removePlayer(player.getUUID());
                if (inst.isEmpty()) {
                    scheduleCleanup(inst);
                }
            }
        }
    }

    public DungeonInstance getInstanceForPosition(BlockPos pos) {
        for (DungeonInstance inst : instances.values()) {
            if (!inst.isActive()) continue;
            if (Math.abs(pos.getX() - inst.getOrigin().getX()) < INSTANCE_SPACING / 2) {
                return inst;
            }
        }
        return null;
    }

    public DungeonInstance getInstanceForPlayer(UUID playerId) {
        UUID instanceId = playerToInstance.get(playerId);
        return instanceId != null ? instances.get(instanceId) : null;
    }

    public DungeonInstance getInstance(UUID instanceId) {
        return instances.get(instanceId);
    }

    public boolean isPlayerInDungeon(UUID playerId) {
        return playerToInstance.containsKey(playerId);
    }

    public void completeInstance(UUID instanceId, MinecraftServer server) {
        DungeonInstance inst = instances.get(instanceId);
        if (inst == null || inst.isCompleted()) return;
        inst.markCompleted();

        int kpReward = inst.getDungeonType().getKnowledgePointReward(inst.getDifficulty());
        ResourceLocation elementUnlock = inst.getDungeonType().getElementUnlock();

        for (UUID playerId : inst.getPlayers()) {
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player == null) continue;

            var data = com.github.runicrebirth.capabilities.dungeon.DungeonData.of(player);
            data.setMaxDifficultyCleared(inst.getDungeonType().getId(), inst.getDifficulty());
            data.addKnowledgePoints(kpReward);
            if (elementUnlock != null) {
                data.unlockElement(elementUnlock);
                ModCriteriaTriggers.ELEMENT_TRIAL.get().trigger(player, elementUnlock);
                player.displayClientMessage(
                        Component.literal("§6Element Unlocked: §f" + elementUnlock.getPath().substring(0, 1).toUpperCase()
                                + elementUnlock.getPath().substring(1) + "!"), false);
            }

            RunicRebirth.LOGGER.info("[Dungeon] Player {} completed {} D{}, earned {} KP",
                    player.getName().getString(), inst.getDungeonType().getId(), inst.getDifficulty(), kpReward);
        }

        deactivatePortal(inst, server);
    }

    private void scheduleCleanup(DungeonInstance inst) {
        inst.deactivate();
        RunicRebirth.LOGGER.info("[Dungeon] Instance {} marked for cleanup", inst.getInstanceId());
    }

    private void deactivatePortal(DungeonInstance inst, MinecraftServer server) {
        BlockPos returnPos = inst.getReturnPos();
        ResourceKey<Level> returnDimKey = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION, inst.getReturnDimension());
        ServerLevel returnLevel = server.getLevel(returnDimKey);
        if (returnLevel == null) return;

        // returnPos is controller pos — find controller, trigger deactivation
        var be = returnLevel.getBlockEntity(returnPos);
        if (be instanceof OculusControllerBlockEntity controller && controller.isActive()) {
            BlockPos portalPos = controller.getPortalPos();
            if (portalPos != null) {
                var portalBe = returnLevel.getBlockEntity(portalPos);
                if (portalBe instanceof OculusPortalBlockEntity portal) {
                    portal.clearSelectedDungeon();
                    portal.setAnimState(OculusPortalBlockEntity.AnimState.DEACTIVATING);
                }
            }
        }
    }

    public void tick(MinecraftServer server) {
        var expired = new java.util.ArrayList<UUID>();
        for (var entry : instances.entrySet()) {
            DungeonInstance inst = entry.getValue();
            if (!inst.isActive()) continue;
            inst.tick();

            if (inst.isTimedOut() && !inst.isCompleted()) {
                RunicRebirth.LOGGER.info("[Dungeon] Instance {} timed out!", inst.getInstanceId());
                for (UUID playerId : new java.util.ArrayList<>(inst.getPlayers())) {
                    ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                    if (player != null) {
                        player.displayClientMessage(Component.literal("§cTime's up! The dungeon collapses around you."), false);
                        DungeonTeleporter.teleportFromDungeon(player, inst);
                    }
                }
                deactivatePortal(inst, server);
                inst.deactivate();
                expired.add(entry.getKey());
            }

            // Periodic time warnings
            int remaining = inst.getRemainingSeconds();
            if (remaining == 300 || remaining == 120 || remaining == 60 || remaining == 30 || remaining == 10) {
                if (inst.getRemainingTicks() % 20 == 0) {
                    for (UUID playerId : inst.getPlayers()) {
                        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                        if (player != null) {
                            player.displayClientMessage(
                                    Component.literal("§e⏱ " + inst.getRemainingTimeFormatted() + " remaining"), true);
                        }
                    }
                }
            }
        }
        instances.values().removeIf(inst -> !inst.isActive() && inst.isEmpty());
    }
}
