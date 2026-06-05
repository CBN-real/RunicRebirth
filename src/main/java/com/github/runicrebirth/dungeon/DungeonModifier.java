package com.github.runicrebirth.dungeon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum DungeonModifier {

    SWIFT_ENEMIES("Swift Enemies", "Enemies move 1.5x faster.", ChatFormatting.YELLOW, -1) {
        @Override
        public void applyToMob(LivingEntity entity) {
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(speed.getBaseValue() * 1.5);
        }
    },
    BLUNT_RESISTANT("Blunt Resistant", "Enemies have 2x blunt resistance.", ChatFormatting.GRAY, -1) {
        @Override
        public void applyToMob(LivingEntity entity) {
            AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
            if (armor != null) armor.setBaseValue(armor.getBaseValue() + 8);
        }
    },
    SHARP_RESISTANT("Sharp Resistant", "Enemies have 2x sharp resistance.", ChatFormatting.GRAY, -1) {
        @Override
        public void applyToMob(LivingEntity entity) {
            AttributeInstance toughness = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughness != null) toughness.setBaseValue(toughness.getBaseValue() + 6);
        }
    },
    MAGIC_RESISTANT("Magic Resistant", "Enemies have 2x magic resistance.", ChatFormatting.DARK_PURPLE, -1) {
        @Override
        public void applyToMob(LivingEntity entity) {
            // No vanilla magic resist attribute — use extra health as proxy
            AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(maxHealth.getBaseValue() * 1.3);
                entity.setHealth(entity.getMaxHealth());
            }
        }
    },
    HEAVY_HITTERS("Heavy Hitters", "Enemies are half as fast but have 1.5x health and damage.", ChatFormatting.RED, -2) {
        @Override
        public void applyToMob(LivingEntity entity) {
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(speed.getBaseValue() * 0.5);
            AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                health.setBaseValue(health.getBaseValue() * 1.5);
                entity.setHealth(entity.getMaxHealth());
            }
            AttributeInstance dmg = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() * 1.5);
        }
    },
    DARKNESS("Darkness", "Reduced visibility.", ChatFormatting.DARK_GRAY, -1) {
        @Override
        public void applyToMob(LivingEntity entity) {}
    },
    SWARM("Swarm", "Spawners produce 50% more enemies.", ChatFormatting.GOLD, -2) {
        @Override
        public void applyToMob(LivingEntity entity) {}
    };

    private final String displayName;
    private final String description;
    private final ChatFormatting color;
    private final int weight; // negative = harder, applied with difficulty scaling

    DungeonModifier(String displayName, String description, ChatFormatting color, int weight) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.weight = weight;
    }

    public abstract void applyToMob(LivingEntity entity);

    public MutableComponent toComponent() {
        return Component.literal("  ▸ " + displayName).withStyle(color)
                .append(Component.literal(" — " + description).withStyle(ChatFormatting.GRAY));
    }

    public String getDisplayName() { return displayName; }
    public int getWeight() { return weight; }

    public static List<DungeonModifier> rollModifiers(int difficulty, Random random) {
        List<DungeonModifier> pool = new ArrayList<>(List.of(values()));
        List<DungeonModifier> result = new ArrayList<>();

        int count = switch (difficulty) {
            case 1 -> random.nextInt(2);       // 0-1 modifiers
            case 2 -> 1 + random.nextInt(2);   // 1-2 modifiers
            case 3 -> 2 + random.nextInt(2);   // 2-3 modifiers
            default -> 0;
        };

        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int idx = random.nextInt(pool.size());
            result.add(pool.remove(idx));
        }
        return result;
    }

    public boolean affectsSpawnerCount() {
        return this == SWARM;
    }

    public float getSpawnerCountMultiplier() {
        return this == SWARM ? 1.5f : 1.0f;
    }
}
