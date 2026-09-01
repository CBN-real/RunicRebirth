package com.github.runicrebirth.advancement;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ModifierKillTracker {

    private static final Map<UUID, List<String>> lastModifiers = new HashMap<>();

    private ModifierKillTracker() {}

    public static void recordCast(ServerPlayer player, List<String> modifiers) {
        if (modifiers.isEmpty()) {
            lastModifiers.remove(player.getUUID());
        } else {
            lastModifiers.put(player.getUUID(), new ArrayList<>(modifiers));
        }
    }

    public static List<String> getModifiers(ServerPlayer player) {
        return lastModifiers.getOrDefault(player.getUUID(), List.of());
    }
}
