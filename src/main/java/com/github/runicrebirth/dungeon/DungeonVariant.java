package com.github.runicrebirth.dungeon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DungeonVariant {

    public record WeightedTemplate(ResourceLocation template, int weight) {}

    public static final String POOL_COMBAT = "runicrebirth:combat";
    public static final String POOL_HALLWAY = "runicrebirth:hallway";
    public static final String POOL_BOSS = "runicrebirth:boss";
    public static final String POOL_INNER_SANCTUM = "runicrebirth:inner_sanctum";

    private final ResourceLocation id;
    private final ResourceLocation tier;
    private final int roomCount;
    private final List<WeightedTemplate> entryRooms;
    private final List<WeightedTemplate> bossRooms;
    private final List<WeightedTemplate> innerSanctumRooms;
    private final List<WeightedTemplate> combatRooms;
    private final List<WeightedTemplate> hallways;

    public DungeonVariant(ResourceLocation id, ResourceLocation tier, int roomCount,
                          List<WeightedTemplate> entryRooms, List<WeightedTemplate> bossRooms,
                          List<WeightedTemplate> innerSanctumRooms, List<WeightedTemplate> combatRooms,
                          List<WeightedTemplate> hallways) {
        this.id = id;
        this.tier = tier;
        this.roomCount = roomCount;
        this.entryRooms = entryRooms;
        this.bossRooms = bossRooms;
        this.innerSanctumRooms = innerSanctumRooms;
        this.combatRooms = combatRooms;
        this.hallways = hallways;
    }

    public static DungeonVariant fromJson(ResourceLocation id, JsonObject obj) {
        ResourceLocation tier = ResourceLocation.parse(obj.get("tier").getAsString());
        int roomCount = obj.get("room_count").getAsInt();

        List<WeightedTemplate> entryRooms = parsePool(obj, "entry_rooms");
        List<WeightedTemplate> bossRooms = parsePool(obj, "boss_rooms");
        List<WeightedTemplate> innerSanctumRooms = parsePool(obj, "inner_sanctum_rooms");
        List<WeightedTemplate> combatRooms = parsePool(obj, "combat_rooms");
        List<WeightedTemplate> hallways = parsePool(obj, "hallways");

        return new DungeonVariant(id, tier, roomCount, entryRooms, bossRooms, innerSanctumRooms, combatRooms, hallways);
    }

    private static List<WeightedTemplate> parsePool(JsonObject obj, String key) {
        List<WeightedTemplate> list = new ArrayList<>();
        if (obj.has(key)) {
            for (var el : obj.getAsJsonArray(key)) {
                var entry = el.getAsJsonObject();
                ResourceLocation template = ResourceLocation.parse(entry.get("template").getAsString());
                int weight = entry.get("weight").getAsInt();
                list.add(new WeightedTemplate(template, weight));
            }
        }
        return list;
    }

    public boolean isValid() {
        return !entryRooms.isEmpty() && !bossRooms.isEmpty() && !innerSanctumRooms.isEmpty();
    }

    public ResourceLocation pickWeighted(List<WeightedTemplate> pool, RandomSource random) {
        int totalWeight = pool.stream().mapToInt(WeightedTemplate::weight).sum();
        if (totalWeight <= 0) return pool.get(0).template();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedTemplate wt : pool) {
            cumulative += wt.weight();
            if (roll < cumulative) return wt.template();
        }
        return pool.get(pool.size() - 1).template();
    }

    public ResourceLocation pickEntryRoom(RandomSource random) {
        return pickWeighted(entryRooms, random);
    }

    public ResourceLocation pickBossRoom(RandomSource random) {
        return pickWeighted(bossRooms, random);
    }

    public ResourceLocation pickInnerSanctumRoom(RandomSource random) {
        return pickWeighted(innerSanctumRooms, random);
    }

    @Nullable
    public ResourceLocation pickFromPool(String poolName, RandomSource random) {
        return switch (poolName) {
            case POOL_COMBAT -> combatRooms.isEmpty() ? null : pickWeighted(combatRooms, random);
            case POOL_HALLWAY -> hallways.isEmpty() ? null : pickWeighted(hallways, random);
            case POOL_BOSS -> pickBossRoom(random);
            case POOL_INNER_SANCTUM -> pickInnerSanctumRoom(random);
            default -> null;
        };
    }

    public ResourceLocation getId() { return id; }
    public ResourceLocation getTier() { return tier; }
    public int getRoomCount() { return roomCount; }
}
