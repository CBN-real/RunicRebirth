package com.github.runicrebirth.client;

import com.github.runicrebirth.network.DungeonDataSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class ClientDungeonData {

    private static Set<ResourceLocation> unlockedElements = new HashSet<>(Set.of(ResourceLocation.parse("runicrebirth:arcane")));
    private static int knowledgePoints = 0;
    private static Set<ResourceLocation> unlockedSpellTypes = new HashSet<>();
    private static Map<ResourceLocation, Integer> maxDifficultyCleared = new HashMap<>();

    private ClientDungeonData() {}

    public static void apply(DungeonDataSyncS2CPacket packet) {
        unlockedElements = new HashSet<>(packet.unlockedElements());
        knowledgePoints = packet.knowledgePoints();
        unlockedSpellTypes = new HashSet<>(packet.unlockedSpellTypes());
        maxDifficultyCleared = new HashMap<>(packet.maxDifficultyCleared());
    }

    public static Set<ResourceLocation> getUnlockedElements() { return unlockedElements; }
    public static int getKnowledgePoints() { return knowledgePoints; }
    public static Set<ResourceLocation> getUnlockedSpellTypes() { return unlockedSpellTypes; }
    public static boolean isElementUnlocked(ResourceLocation id) { return unlockedElements.contains(id); }

    public static int getMaxDifficultyCleared(ResourceLocation dungeonId) {
        return maxDifficultyCleared.getOrDefault(dungeonId, 0);
    }

    public static int getMaxSelectableDifficulty(ResourceLocation dungeonId, int cap) {
        return Math.min(getMaxDifficultyCleared(dungeonId) + 1, cap);
    }
}
