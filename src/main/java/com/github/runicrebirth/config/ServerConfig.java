package com.github.runicrebirth.config;

import com.github.runicrebirth.RunicRebirth;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue SPELL_STACK_COUNT;
    public static final ModConfigSpec.IntValue GLOBAL_CAST_LOCKOUT_TICKS;
    public static final ModConfigSpec.IntValue MAGIC_PROJECTILE_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_PROJECTILE_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_BEAM_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_BEAM_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_BLAST_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_BLAST_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_ARROW_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_ARROW_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_EXPLOSION_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_EXPLOSION_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_SLASH_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_SLASH_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_METEOR_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_METEOR_BASE_DAMAGE;
    public static final ModConfigSpec.DoubleValue MAGIC_METEOR_SPLASH_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_SHIELD_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_SHIELD_BASE_HEALTH;
    public static final ModConfigSpec.IntValue MAGIC_SHIELD_DURATION_TICKS;
    public static final ModConfigSpec.IntValue MAGIC_HAMMER_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_HAMMER_DIRECT_DAMAGE;
    public static final ModConfigSpec.DoubleValue MAGIC_HAMMER_SPLASH_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_BINDING_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_BINDING_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue MAGIC_BALLISTA_COOLDOWN;
    public static final ModConfigSpec.DoubleValue MAGIC_BALLISTA_BASE_DAMAGE;
    public static final ModConfigSpec.IntValue RING_OF_EXPANSION_DELTA;
    public static final ModConfigSpec.IntValue ADDITIVE_SIZE_DELTA;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("stacks");
        SPELL_STACK_COUNT = b.comment("Number of parallel spell stacks per player").defineInRange("spellStackCount", 2, 1, 8);
        GLOBAL_CAST_LOCKOUT_TICKS = b.comment("General post-cast lockout ticks; blocks all casts for this many ticks").defineInRange("globalCastLockoutTicks", 5, 0, 100);
        b.pop();

        b.push("spells");
        b.push("magic_projectile");
        MAGIC_PROJECTILE_COOLDOWN = b.defineInRange("cooldownTicks", 40, 0, 6000);
        MAGIC_PROJECTILE_BASE_DAMAGE = b.defineInRange("baseDamage", 5.0, 0.0, 100.0);
        b.pop();
        b.push("magic_beam");
        MAGIC_BEAM_COOLDOWN = b.defineInRange("cooldownTicks", 80, 0, 6000);
        MAGIC_BEAM_BASE_DAMAGE = b.defineInRange("baseDamage", 3.0, 0.0, 100.0);
        b.pop();
        b.push("magic_blast");
        MAGIC_BLAST_COOLDOWN = b.defineInRange("cooldownTicks", 120, 0, 6000);
        MAGIC_BLAST_BASE_DAMAGE = b.defineInRange("baseDamage", 2.5, 0.0, 100.0);
        b.pop();
        b.push("magic_arrow");
        MAGIC_ARROW_COOLDOWN = b.defineInRange("cooldownTicks", 60, 0, 6000);
        MAGIC_ARROW_BASE_DAMAGE = b.defineInRange("baseDamage", 6.0, 0.0, 100.0);
        b.pop();
        b.push("magic_explosion");
        MAGIC_EXPLOSION_COOLDOWN = b.defineInRange("cooldownTicks", 100, 0, 6000);
        MAGIC_EXPLOSION_BASE_DAMAGE = b.defineInRange("baseDamage", 7.0, 0.0, 100.0);
        b.pop();
        b.push("magic_slash");
        MAGIC_SLASH_COOLDOWN = b.defineInRange("cooldownTicks", 80, 0, 6000);
        MAGIC_SLASH_BASE_DAMAGE = b.defineInRange("baseDamage", 4.0, 0.0, 100.0);
        b.pop();
        b.push("magic_meteor");
        MAGIC_METEOR_COOLDOWN = b.defineInRange("cooldownTicks", 160, 0, 6000);
        MAGIC_METEOR_BASE_DAMAGE = b.defineInRange("baseDamage", 10.0, 0.0, 100.0);
        MAGIC_METEOR_SPLASH_DAMAGE = b.defineInRange("splashDamage", 5.0, 0.0, 100.0);
        b.pop();
        b.push("magic_shield");
        MAGIC_SHIELD_COOLDOWN = b.defineInRange("cooldownTicks", 200, 0, 6000);
        MAGIC_SHIELD_BASE_HEALTH = b.defineInRange("baseHealth", 20.0, 1.0, 200.0);
        MAGIC_SHIELD_DURATION_TICKS = b.defineInRange("durationTicks", 600, 20, 12000);
        b.pop();
        b.push("magic_hammer");
        MAGIC_HAMMER_COOLDOWN = b.defineInRange("cooldownTicks", 200, 0, 6000);
        MAGIC_HAMMER_DIRECT_DAMAGE = b.defineInRange("directDamage", 12.0, 0.0, 100.0);
        MAGIC_HAMMER_SPLASH_DAMAGE = b.defineInRange("splashDamage", 6.0, 0.0, 100.0);
        b.pop();
        b.push("magic_binding");
        MAGIC_BINDING_COOLDOWN = b.defineInRange("cooldownTicks", 160, 0, 6000);
        MAGIC_BINDING_BASE_DAMAGE = b.defineInRange("baseDamage", 6.0, 0.0, 100.0);
        b.pop();
        b.push("magic_ballista");
        MAGIC_BALLISTA_COOLDOWN = b.defineInRange("cooldownTicks", 120, 0, 6000);
        MAGIC_BALLISTA_BASE_DAMAGE = b.defineInRange("baseDamage", 8.0, 0.0, 100.0);
        b.pop();
        b.pop();

        b.push("modifiers");
        ADDITIVE_SIZE_DELTA = b.defineInRange("additiveSizeDelta", 1, 0, 100);
        RING_OF_EXPANSION_DELTA = b.defineInRange("ringOfExpansionSizeDelta", 3, 0, 100);
        b.pop();

        SPEC = b.build();
    }

    private ServerConfig() {}

    public static void register(net.neoforged.fml.ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        RunicRebirth.LOGGER.info("[RunicRebirth] ServerConfig loaded");
    }
}
