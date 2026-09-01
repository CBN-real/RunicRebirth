package com.github.runicrebirth.client;

import com.github.runicrebirth.network.DungeonDataSyncS2CPacket;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ClientDungeonData {

    private static Set<Identifier> unlockedElements = new HashSet<>(Set.of(Identifier.parse("runicrebirth:arcane")));
    private static int knowledgePoints = 0;
    private static Set<Identifier> unlockedSpellTypes = new HashSet<>();
    private static Map<Identifier, Integer> maxDifficultyCleared = new HashMap<>();
    private static Set<Identifier> unlockedEntries = new HashSet<>();

    private ClientDungeonData() {}

    public static void apply(DungeonDataSyncS2CPacket packet) {
        unlockedElements = new HashSet<>(packet.unlockedElements());
        knowledgePoints = packet.knowledgePoints();
        unlockedSpellTypes = new HashSet<>(packet.unlockedSpellTypes());
        maxDifficultyCleared = new HashMap<>(packet.maxDifficultyCleared());
        unlockedEntries = new HashSet<>(packet.unlockedEntries());
    }

    public static Set<Identifier> getUnlockedElements() { return unlockedElements; }
    public static int getKnowledgePoints() { return knowledgePoints; }
    public static Set<Identifier> getUnlockedSpellTypes() { return unlockedSpellTypes; }
    public static boolean isElementUnlocked(Identifier id) { return unlockedElements.contains(id); }
    public static boolean isUnlocked(Identifier id) { return unlockedEntries.contains(id); }
    public static Set<Identifier> getUnlockedEntries() { return java.util.Collections.unmodifiableSet(unlockedEntries); }

    public static int getMaxDifficultyCleared(Identifier dungeonId) {
        return maxDifficultyCleared.getOrDefault(dungeonId, 0);
    }

    public static int getMaxSelectableDifficulty(Identifier dungeonId, int cap) {
        return Math.min(getMaxDifficultyCleared(dungeonId) + 1, cap);
    }
}
