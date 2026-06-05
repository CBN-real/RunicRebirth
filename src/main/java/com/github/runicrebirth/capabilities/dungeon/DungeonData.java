package com.github.runicrebirth.capabilities.dungeon;

import com.github.runicrebirth.init.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DungeonData {

    public static final Codec<DungeonData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                    .optionalFieldOf("max_difficulty_cleared", Map.of())
                    .forGetter(d -> Map.copyOf(d.maxDifficultyCleared)),
            ResourceLocation.CODEC.listOf()
                    .<Set<ResourceLocation>>xmap(HashSet::new, l -> l.stream().toList())
                    .optionalFieldOf("unlocked_elements", new HashSet<>(Set.of(ResourceLocation.parse("runicrebirth:arcane"))))
                    .forGetter(d -> d.unlockedElements),
            Codec.INT.optionalFieldOf("knowledge_points", 0)
                    .forGetter(d -> d.knowledgePoints),
            ResourceLocation.CODEC.listOf()
                    .<Set<ResourceLocation>>xmap(HashSet::new, l -> l.stream().toList())
                    .optionalFieldOf("unlocked_spell_types", new HashSet<>())
                    .forGetter(d -> d.unlockedSpellTypes)
    ).apply(inst, DungeonData::fromCodec));

    private final Map<ResourceLocation, Integer> maxDifficultyCleared;
    private final Set<ResourceLocation> unlockedElements;
    private int knowledgePoints;
    private final Set<ResourceLocation> unlockedSpellTypes;

    public DungeonData() {
        this.maxDifficultyCleared = new HashMap<>();
        this.unlockedElements = new HashSet<>(Set.of(ResourceLocation.parse("runicrebirth:arcane")));
        this.knowledgePoints = 0;
        this.unlockedSpellTypes = new HashSet<>();
    }

    private static DungeonData fromCodec(Map<ResourceLocation, Integer> cleared,
                                          Set<ResourceLocation> elements,
                                          int kp,
                                          Set<ResourceLocation> spellTypes) {
        var data = new DungeonData();
        data.maxDifficultyCleared.putAll(cleared);
        data.unlockedElements.clear();
        data.unlockedElements.addAll(elements);
        data.knowledgePoints = kp;
        data.unlockedSpellTypes.addAll(spellTypes);
        return data;
    }

    public static DungeonData of(Player player) {
        return player.getData(ModAttachments.DUNGEON_DATA);
    }

    public int getMaxDifficultyCleared(ResourceLocation dungeonId) {
        return maxDifficultyCleared.getOrDefault(dungeonId, 0);
    }

    public void setMaxDifficultyCleared(ResourceLocation dungeonId, int difficulty) {
        int current = maxDifficultyCleared.getOrDefault(dungeonId, 0);
        if (difficulty > current) {
            maxDifficultyCleared.put(dungeonId, difficulty);
        }
    }

    public int getMaxSelectableDifficulty(ResourceLocation dungeonId, int cap) {
        return Math.min(getMaxDifficultyCleared(dungeonId) + 1, cap);
    }

    public Set<ResourceLocation> getUnlockedElements() {
        return unlockedElements;
    }

    public boolean isElementUnlocked(ResourceLocation elementId) {
        return unlockedElements.contains(elementId);
    }

    public void unlockElement(ResourceLocation elementId) {
        unlockedElements.add(elementId);
    }

    public int getKnowledgePoints() {
        return knowledgePoints;
    }

    public void addKnowledgePoints(int amount) {
        knowledgePoints += amount;
    }

    public boolean spendKnowledgePoints(int amount) {
        if (knowledgePoints < amount) return false;
        knowledgePoints -= amount;
        return true;
    }

    public Set<ResourceLocation> getUnlockedSpellTypes() {
        return unlockedSpellTypes;
    }

    public boolean isSpellTypeUnlocked(ResourceLocation spellTypeId) {
        return unlockedSpellTypes.contains(spellTypeId);
    }

    public void unlockSpellType(ResourceLocation spellTypeId) {
        unlockedSpellTypes.add(spellTypeId);
    }
}
