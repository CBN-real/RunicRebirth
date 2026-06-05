package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum DungeonType {

    FIRE_TRIAL("fire_trial", "Fire Trial", GenerationMode.PRESET, 1, null,
            "Defeat the fire guardians to unlock the Fire element."),
    ICE_TRIAL("ice_trial", "Ice Trial", GenerationMode.PRESET, 1, null,
            "Defeat the ice guardians to unlock the Ice element."),
    WIND_TRIAL("wind_trial", "Wind Trial", GenerationMode.PRESET, 1, null,
            "Defeat the wind guardians to unlock the Wind element."),
    EARTH_TRIAL("earth_trial", "Earth Trial", GenerationMode.PRESET, 1, null,
            "Defeat the earth guardians to unlock the Earth element."),
    ACOLYTE("acolyte", "Acolyte Dungeon", GenerationMode.PROCEDURAL, 3, null,
            "A shifting labyrinth of arcane trials. Difficulty scales with your ambition.");

    private final ResourceLocation id;
    private final String displayName;
    private final GenerationMode generationMode;
    private final int maxDifficulty;
    private final ResourceLocation requiredElement;
    private final String description;

    DungeonType(String path, String displayName, GenerationMode generationMode,
                int maxDifficulty, ResourceLocation requiredElement, String description) {
        this.id = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, path);
        this.displayName = displayName;
        this.generationMode = generationMode;
        this.maxDifficulty = maxDifficulty;
        this.requiredElement = requiredElement;
        this.description = description;
    }

    public ResourceLocation getId() { return id; }
    public String getDisplayName() { return displayName; }
    public GenerationMode getGenerationMode() { return generationMode; }
    public int getMaxDifficulty() { return maxDifficulty; }
    public ResourceLocation getRequiredElement() { return requiredElement; }
    public String getDescription() { return description; }

    public Component getDisplayComponent() {
        return Component.translatable("dungeon.runicrebirth." + id.getPath());
    }

    public int getKnowledgePointReward(int difficulty) {
        return switch (this) {
            case FIRE_TRIAL, ICE_TRIAL, WIND_TRIAL, EARTH_TRIAL -> 1;
            case ACOLYTE -> switch (difficulty) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 4;
                default -> 0;
            };
        };
    }

    public ResourceLocation getElementUnlock() {
        return switch (this) {
            case FIRE_TRIAL -> ResourceLocation.parse("runicrebirth:fire");
            case ICE_TRIAL -> ResourceLocation.parse("runicrebirth:ice");
            case WIND_TRIAL -> ResourceLocation.parse("runicrebirth:wind");
            case EARTH_TRIAL -> ResourceLocation.parse("runicrebirth:earth");
            default -> null;
        };
    }

    public static DungeonType byId(ResourceLocation id) {
        for (DungeonType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return null;
    }

    public enum GenerationMode {
        PRESET,
        PROCEDURAL
    }
}
