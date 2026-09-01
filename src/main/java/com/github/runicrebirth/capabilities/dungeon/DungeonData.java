package com.github.runicrebirth.capabilities.dungeon;

import com.github.runicrebirth.init.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DungeonData {

    public static final MapCodec<DungeonData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.unboundedMap(Identifier.CODEC, Codec.INT)
                    .optionalFieldOf("max_difficulty_cleared", Map.of())
                    .forGetter(d -> Map.copyOf(d.maxDifficultyCleared)),
            Identifier.CODEC.listOf()
                    .<Set<Identifier>>xmap(HashSet::new, l -> l.stream().toList())
                    .optionalFieldOf("unlocked_elements", new HashSet<>(Set.of(Identifier.parse("runicrebirth:arcane"))))
                    .forGetter(d -> d.unlockedElements),
            Codec.INT.optionalFieldOf("knowledge_points", 0)
                    .forGetter(d -> d.knowledgePoints),
            Identifier.CODEC.listOf()
                    .<Set<Identifier>>xmap(HashSet::new, l -> l.stream().toList())
                    .optionalFieldOf("unlocked_spell_types", new HashSet<>())
                    .forGetter(d -> d.unlockedSpellTypes),
            Identifier.CODEC.listOf()
                    .<Set<Identifier>>xmap(HashSet::new, l -> l.stream().toList())
                    .optionalFieldOf("unlocked_entries", new HashSet<>())
                    .forGetter(d -> d.unlockedEntries),
            Codec.LONG.optionalFieldOf("spells_drawn", 0L)
                    .forGetter(d -> d.spellsDrawn),
            Codec.LONG.optionalFieldOf("spells_cast", 0L)
                    .forGetter(d -> d.spellsCast),
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .optionalFieldOf("modifier_kill_counts", Map.of())
                    .forGetter(d -> Map.copyOf(d.modifierKillCounts)),
            Codec.INT.optionalFieldOf("magic_weapon_kills", 0)
                    .forGetter(d -> d.magicWeaponKills),
            Codec.INT.optionalFieldOf("weapon_active_uses", 0)
                    .forGetter(d -> d.weaponActiveUses),
            Codec.INT.optionalFieldOf("ring_activations", 0)
                    .forGetter(d -> d.ringActivations),
            Codec.INT.optionalFieldOf("drone_kills", 0)
                    .forGetter(d -> d.droneKills)
    ).apply(inst, DungeonData::fromCodec));

    private final Map<Identifier, Integer> maxDifficultyCleared;
    private final Set<Identifier> unlockedElements;
    private int knowledgePoints;
    private final Set<Identifier> unlockedSpellTypes;
    private final Set<Identifier> unlockedEntries;
    private long spellsDrawn;
    private long spellsCast;
    private final Map<String, Integer> modifierKillCounts;
    private int magicWeaponKills;
    private int weaponActiveUses;
    private int ringActivations;
    private int droneKills;

    public DungeonData() {
        this.maxDifficultyCleared = new HashMap<>();
        this.unlockedElements = new HashSet<>(Set.of(Identifier.parse("runicrebirth:arcane")));
        this.knowledgePoints = 0;
        this.unlockedSpellTypes = new HashSet<>();
        this.unlockedEntries = new HashSet<>();
        this.spellsDrawn = 0L;
        this.spellsCast = 0L;
        this.modifierKillCounts = new HashMap<>();
        this.magicWeaponKills = 0;
        this.weaponActiveUses = 0;
        this.ringActivations = 0;
        this.droneKills = 0;
    }

    private static DungeonData fromCodec(Map<Identifier, Integer> cleared,
                                          Set<Identifier> elements,
                                          int kp,
                                          Set<Identifier> spellTypes,
                                          Set<Identifier> entries,
                                          long spellsDrawn,
                                          long spellsCast,
                                          Map<String, Integer> modifierKillCounts,
                                          int magicWeaponKills,
                                          int weaponActiveUses,
                                          int ringActivations,
                                          int droneKills) {
        var data = new DungeonData();
        data.maxDifficultyCleared.putAll(cleared);
        data.unlockedElements.clear();
        data.unlockedElements.addAll(elements);
        data.knowledgePoints = kp;
        data.unlockedSpellTypes.addAll(spellTypes);
        data.unlockedEntries.addAll(entries);
        data.spellsDrawn = spellsDrawn;
        data.spellsCast = spellsCast;
        data.modifierKillCounts.putAll(modifierKillCounts);
        data.magicWeaponKills = magicWeaponKills;
        data.weaponActiveUses = weaponActiveUses;
        data.ringActivations = ringActivations;
        data.droneKills = droneKills;
        return data;
    }

    public static DungeonData of(Player player) {
        return player.getData(ModAttachments.DUNGEON_DATA);
    }

    public int getMaxDifficultyCleared(Identifier dungeonId) {
        return maxDifficultyCleared.getOrDefault(dungeonId, 0);
    }

    public void setMaxDifficultyCleared(Identifier dungeonId, int difficulty) {
        int current = maxDifficultyCleared.getOrDefault(dungeonId, 0);
        if (difficulty > current) {
            maxDifficultyCleared.put(dungeonId, difficulty);
        }
    }

    public int getMaxSelectableDifficulty(Identifier dungeonId, int cap) {
        return Math.min(getMaxDifficultyCleared(dungeonId) + 1, cap);
    }

    public Set<Identifier> getUnlockedElements() {
        return unlockedElements;
    }

    public boolean isElementUnlocked(Identifier elementId) {
        return unlockedElements.contains(elementId);
    }

    public void unlockElement(Identifier elementId) {
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

    public Set<Identifier> getUnlockedSpellTypes() {
        return unlockedSpellTypes;
    }

    public boolean isSpellTypeUnlocked(Identifier spellTypeId) {
        return unlockedSpellTypes.contains(spellTypeId);
    }

    public void unlockSpellType(Identifier spellTypeId) {
        unlockedSpellTypes.add(spellTypeId);
    }

    public Set<Identifier> getUnlockedEntries() {
        return unlockedEntries;
    }

    public boolean isUnlocked(Identifier entryId) {
        return unlockedEntries.contains(entryId);
    }

    public void unlockEntry(Identifier entryId) {
        unlockedEntries.add(entryId);
    }

    public void clearUnlockedEntries() {
        unlockedEntries.clear();
    }

    public long getSpellsDrawn() { return spellsDrawn; }
    public long incrementSpellsDrawn() { return ++spellsDrawn; }

    public long getSpellsCast() { return spellsCast; }
    public long incrementSpellsCast() { return ++spellsCast; }

    public int getModifierKills(String modifierPath) {
        return modifierKillCounts.getOrDefault(modifierPath, 0);
    }

    public int incrementModifierKills(String modifierPath) {
        int next = modifierKillCounts.getOrDefault(modifierPath, 0) + 1;
        modifierKillCounts.put(modifierPath, next);
        return next;
    }

    public int getMagicWeaponKills() { return magicWeaponKills; }
    public int incrementMagicWeaponKills() { return ++magicWeaponKills; }

    public int getWeaponActiveUses() { return weaponActiveUses; }
    public int incrementWeaponActiveUses() { return ++weaponActiveUses; }

    public int getRingActivations() { return ringActivations; }
    public int incrementRingActivations() { return ++ringActivations; }

    public int getDroneKills() { return droneKills; }
    public int incrementDroneKills() { return ++droneKills; }
}
