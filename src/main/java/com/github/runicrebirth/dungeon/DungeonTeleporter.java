package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class DungeonTeleporter {

    private DungeonTeleporter() {}

    public static void teleportToDungeon(ServerPlayer player, DungeonInstance instance) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;

        ServerLevel dungeonLevel = server.getLevel(ModDimensions.DUNGEON_LEVEL);
        if (dungeonLevel == null) {
            RunicRebirth.LOGGER.error("[Dungeon] Dungeon dimension not found!");
            return;
        }

        BlockPos spawnPos = instance.getEntryPortalPos() != null
                ? instance.getEntryPortalPos().above()
                : instance.getOrigin().above();
        player.teleportTo(dungeonLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);

        RunicRebirth.LOGGER.info("[Dungeon] Teleported {} to dungeon instance at {}",
                player.getName().getString(), spawnPos);
    }

    public static void teleportFromDungeon(ServerPlayer player, DungeonInstance instance) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;

        ResourceKey<Level> returnDimKey = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION, instance.getReturnDimension());
        ServerLevel returnLevel = server.getLevel(returnDimKey);
        if (returnLevel == null) {
            returnLevel = server.overworld();
        }

        BlockPos returnPos = instance.getReturnPos();
        player.teleportTo(returnLevel, returnPos.getX() + 0.5, returnPos.getY() + 1.0, returnPos.getZ() + 0.5,
                java.util.Set.of(), player.getYRot(), player.getXRot(), false);

        DungeonInstanceManager.get().leaveInstance(player);
        DungeonEventHandler.onLeaveDungeon(player);

        RunicRebirth.LOGGER.info("[Dungeon] Returned {} to {} at {}",
                player.getName().getString(), instance.getReturnDimension(), returnPos);
    }
}
