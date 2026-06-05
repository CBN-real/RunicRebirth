package com.github.runicrebirth.dungeon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class DifficultyScaler {

    private DifficultyScaler() {}

    public static void applyDifficulty(LivingEntity entity, int difficulty) {
        float healthMult = getHealthMultiplier(difficulty);
        float damageMult = getDamageMultiplier(difficulty);

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            double base = maxHealth.getBaseValue();
            maxHealth.setBaseValue(base * healthMult);
            entity.setHealth(entity.getMaxHealth());
        }

        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            double base = attackDamage.getBaseValue();
            attackDamage.setBaseValue(base * damageMult);
        }
    }

    private static float getHealthMultiplier(int difficulty) {
        return switch (difficulty) {
            case 1 -> 1.0f;
            case 2 -> 1.5f;
            case 3 -> 2.5f;
            default -> 1.0f;
        };
    }

    private static float getDamageMultiplier(int difficulty) {
        return switch (difficulty) {
            case 1 -> 1.0f;
            case 2 -> 1.25f;
            case 3 -> 1.75f;
            default -> 1.0f;
        };
    }
}
