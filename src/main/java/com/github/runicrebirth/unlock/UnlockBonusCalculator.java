package com.github.runicrebirth.unlock;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public final class UnlockBonusCalculator {

    private UnlockBonusCalculator() {}

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath("runicrebirth", path);
    }

    public static float getSpellDamageMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("wizard_runic_mastery_5"))) return 2.50f;
        if (d.isUnlocked(rl("wizard_runic_mastery_4"))) return 1.75f;
        if (d.isUnlocked(rl("wizard_runic_mastery_3"))) return 1.50f;
        if (d.isUnlocked(rl("wizard_runic_mastery_2"))) return 1.20f;
        if (d.isUnlocked(rl("wizard_runic_mastery_1"))) return 1.10f;
        return 1.0f;
    }

    public static float getSpellSizeMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("arcanum_calligraphy_5"))) return 3.00f;
        if (d.isUnlocked(rl("arcanum_calligraphy_4"))) return 2.00f;
        if (d.isUnlocked(rl("arcanum_calligraphy_3"))) return 1.75f;
        if (d.isUnlocked(rl("arcanum_calligraphy_2"))) return 1.50f;
        if (d.isUnlocked(rl("arcanum_calligraphy_1"))) return 1.25f;
        return 1.0f;
    }

    public static float getSpellCooldownMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("mage_earths_flow_5"))) return 0.50f;
        if (d.isUnlocked(rl("mage_earths_flow_4"))) return 0.70f;
        if (d.isUnlocked(rl("mage_earths_flow_3"))) return 0.80f;
        if (d.isUnlocked(rl("mage_earths_flow_2"))) return 0.90f;
        if (d.isUnlocked(rl("mage_earths_flow_1"))) return 0.95f;
        return 1.0f;
    }

    public static int getExtraChargesBonus(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("mage_earths_abundance_5"))) return 10;
        if (d.isUnlocked(rl("mage_earths_abundance_4"))) return 6;
        if (d.isUnlocked(rl("mage_earths_abundance_3"))) return 4;
        if (d.isUnlocked(rl("mage_earths_abundance_2"))) return 2;
        if (d.isUnlocked(rl("mage_earths_abundance_1"))) return 1;
        return 0;
    }

    public static void applySpellBonuses(Player player, SpellParams params) {
        params.damage *= getSpellDamageMultiplier(player);
        params.size *= getSpellSizeMultiplier(player);
        params.chargesBonus += getExtraChargesBonus(player);
    }

    public static float getAuraDamageMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("runeblade_martial_aura_5"))) return 2.50f;
        if (d.isUnlocked(rl("runeblade_martial_aura_4"))) return 1.75f;
        if (d.isUnlocked(rl("runeblade_martial_aura_3"))) return 1.50f;
        if (d.isUnlocked(rl("runeblade_martial_aura_2"))) return 1.20f;
        if (d.isUnlocked(rl("runeblade_martial_aura_1"))) return 1.10f;
        return 1.0f;
    }

    public static float getWeaponActiveCooldownMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("runeblade_vigsalmaestro_5"))) return 0.50f;
        if (d.isUnlocked(rl("runeblade_vigsalmaestro_4"))) return 0.70f;
        if (d.isUnlocked(rl("runeblade_vigsalmaestro_3"))) return 0.80f;
        if (d.isUnlocked(rl("runeblade_vigsalmaestro_2"))) return 0.90f;
        if (d.isUnlocked(rl("runeblade_vigsalmaestro_1"))) return 0.95f;
        return 1.0f;
    }

    public static float getRingCooldownMultiplier(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("artificer_utility_hammer_5"))) return 0.50f;
        if (d.isUnlocked(rl("artificer_utility_hammer_4"))) return 0.70f;
        if (d.isUnlocked(rl("artificer_utility_hammer_3"))) return 0.80f;
        if (d.isUnlocked(rl("artificer_utility_hammer_2"))) return 0.90f;
        if (d.isUnlocked(rl("artificer_utility_hammer_1"))) return 0.95f;
        return 1.0f;
    }

    public static int getExtraDroneSlots(Player player) {
        DungeonData d = DungeonData.of(player);
        if (d.isUnlocked(rl("artificer_commander_yotor_3"))) return 4;
        if (d.isUnlocked(rl("artificer_commander_yotor_2"))) return 2;
        if (d.isUnlocked(rl("artificer_commander_yotor_1"))) return 1;
        return 0;
    }
}
