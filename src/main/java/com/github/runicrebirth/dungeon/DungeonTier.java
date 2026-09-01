package com.github.runicrebirth.dungeon;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DungeonTier {

    public record WeightedModifier(String modifierName, int weight) {}

    private final Identifier id;
    private final int knowledgePointReward;
    @Nullable
    private final Identifier elementUnlock;
    private final List<WeightedModifier> modifiers;
    private final List<Identifier> variants;

    public DungeonTier(Identifier id, int knowledgePointReward,
                       @Nullable Identifier elementUnlock,
                       List<WeightedModifier> modifiers,
                       List<Identifier> variants) {
        this.id = id;
        this.knowledgePointReward = knowledgePointReward;
        this.elementUnlock = elementUnlock;
        this.modifiers = modifiers;
        this.variants = variants;
    }

    public static DungeonTier fromJson(Identifier id, JsonObject obj) {
        int kpReward = obj.get("knowledge_point_reward").getAsInt();

        Identifier elementUnlock = null;
        if (obj.has("element_unlock") && !obj.get("element_unlock").isJsonNull()) {
            elementUnlock = Identifier.parse(obj.get("element_unlock").getAsString());
        }

        List<WeightedModifier> modifiers = new ArrayList<>();
        if (obj.has("modifiers")) {
            for (var el : obj.getAsJsonArray("modifiers")) {
                var mod = el.getAsJsonObject();
                String modifierName = mod.get("modifier").getAsString();
                int weight = mod.get("weight").getAsInt();
                modifiers.add(new WeightedModifier(modifierName, weight));
            }
        }

        List<Identifier> variants = new ArrayList<>();
        if (obj.has("variants")) {
            for (var el : obj.getAsJsonArray("variants")) {
                variants.add(Identifier.parse(el.getAsString()));
            }
        }

        return new DungeonTier(id, kpReward, elementUnlock, modifiers, variants);
    }

    public List<DungeonModifier> rollModifiers(RandomSource random, int difficulty) {
        int count = switch (difficulty) {
            case 1 -> random.nextInt(2);
            case 2 -> 1 + random.nextInt(2);
            case 3 -> 2 + random.nextInt(2);
            default -> 0;
        };

        List<WeightedModifier> available = new ArrayList<>(this.modifiers);
        List<DungeonModifier> result = new ArrayList<>();

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            int totalWeight = available.stream().mapToInt(WeightedModifier::weight).sum();
            if (totalWeight <= 0) break;
            int roll = random.nextInt(totalWeight);
            int cumulative = 0;
            for (int j = 0; j < available.size(); j++) {
                cumulative += available.get(j).weight();
                if (roll < cumulative) {
                    try {
                        DungeonModifier modifier = DungeonModifier.valueOf(available.get(j).modifierName().toUpperCase());
                        result.add(modifier);
                    } catch (IllegalArgumentException e) {
                        // skip unknown
                    }
                    available.remove(j);
                    break;
                }
            }
        }

        return result;
    }

    @Nullable
    public Identifier pickVariant(RandomSource random) {
        if (variants.isEmpty()) return null;
        return variants.get(random.nextInt(variants.size()));
    }

    public Identifier getId() { return id; }
    public int getKnowledgePointReward() { return knowledgePointReward; }
    @Nullable
    public Identifier getElementUnlock() { return elementUnlock; }
}
