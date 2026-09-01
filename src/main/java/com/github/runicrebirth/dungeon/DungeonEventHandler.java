package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.network.DungeonDataSyncS2CPacket;
// TODO: ChatFormatting may be removed in 26.1.2 — if so, replace .withStyle(ChatFormatting.X) with .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xRRGGBB)))
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public class DungeonEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!DungeonInstanceManager.get().isPlayerInDungeon(player.getUUID())) return;

        DungeonInstance instance = DungeonInstanceManager.get().getInstanceForPlayer(player.getUUID());
        if (instance == null) return;

        // KeepInventory in dungeon — cancel vanilla death drops
        // NeoForge: items are kept via gamerule or manual restoration
        // We handle this by setting keepInventory gamerule behavior below

        // Apply death time penalty
        instance.applyDeathPenalty();
        player.sendSystemMessage(
                Component.literal("§c-1 minute! " + instance.getRemainingTimeFormatted() + " remaining."), false);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        DungeonInstance instance = DungeonInstanceManager.get().getInstanceForPlayer(player.getUUID());
        if (instance == null) return;

        if (instance.isTimedOut() || !instance.isActive()) {
            DungeonTeleporter.teleportFromDungeon(player, instance);
            restoreGameMode(player);
        } else {
            // Respawn back in dungeon at origin
            var origin = instance.getOrigin().above();
            var server = player.level().getServer();
            var dungeonLevel = server != null ? server.getLevel(ModDimensions.DUNGEON_LEVEL) : null;
            if (dungeonLevel != null) {
                player.teleportTo(dungeonLevel, origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5,
                        java.util.Set.of(), player.getYRot(), player.getXRot(), false);
                player.setGameMode(GameType.ADVENTURE);
            }
        }
        DungeonDataSyncS2CPacket.sendTo(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!DungeonInstanceManager.get().isPlayerInDungeon(player.getUUID())) return;
        DungeonInstanceManager.get().leaveInstance(player);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.level().dimension().equals(ModDimensions.DUNGEON_LEVEL)) {
            if (!DungeonInstanceManager.get().isPlayerInDungeon(player.getUUID())) {
                var srv = player.level().getServer();
                if (srv != null) player.teleportTo(srv.overworld(),
                        player.getX(), 64, player.getZ(), java.util.Set.of(), player.getYRot(), player.getXRot(), false);
                restoreGameMode(player);
            } else {
                player.setGameMode(GameType.ADVENTURE);
            }
        }

        DungeonDataSyncS2CPacket.sendTo(player);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        DungeonInstanceManager.get().tick(event.getServer());
    }

    public static void onEnterDungeon(ServerPlayer player, DungeonInstance instance) {
        // Set adventure mode
        player.setGameMode(GameType.ADVENTURE);

        // Announce dungeon entry
        player.sendSystemMessage(Component.literal(""), false);
        String tierName = instance.getTierId().getPath();
        player.sendSystemMessage(
                Component.literal("§6§l═══ " + tierName
                        + " — Difficulty " + instance.getDifficulty() + " §6§l═══"), false);
        player.sendSystemMessage(
                Component.literal("§7Time limit: §e" + instance.getRemainingTimeFormatted()), false);
        player.sendSystemMessage(
                Component.literal("§7Death penalty: §c-1 minute per death"), false);

        // Announce modifiers
        if (!instance.getModifiers().isEmpty()) {
            player.sendSystemMessage(Component.literal("§7Modifiers:"), false);
            for (DungeonModifier mod : instance.getModifiers()) {
                player.sendSystemMessage(mod.toComponent(), false);
            }
        } else {
            player.sendSystemMessage(
                    Component.literal("§7No modifiers active.").withStyle(ChatFormatting.ITALIC), false);
        }
        player.sendSystemMessage(Component.literal(""), false);
    }

    public static void onLeaveDungeon(ServerPlayer player) {
        restoreGameMode(player);
    }

    private static void restoreGameMode(ServerPlayer player) {
        player.setGameMode(GameType.SURVIVAL);
    }
}
