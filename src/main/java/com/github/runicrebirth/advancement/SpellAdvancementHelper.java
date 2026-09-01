package com.github.runicrebirth.advancement;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellType;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class SpellAdvancementHelper {

    private SpellAdvancementHelper() {}

    public static Identifier advancementIdFor(SpellType type) {
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spells/" + type.id().getPath());
    }

    public static Identifier advancementIdFor(Identifier spellTypeId) {
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spells/" + spellTypeId.getPath());
    }

    public static boolean hasSpellUnlocked(ServerPlayer player, SpellType type) {
        return hasAdvancement(player, advancementIdFor(type));
    }

    public static boolean hasAdvancement(ServerPlayer player, Identifier advancementId) {
        net.minecraft.server.MinecraftServer server = ((net.minecraft.server.level.ServerLevel) player.level()).getServer();
        AdvancementHolder holder = server.getAdvancements().get(advancementId);
        if (holder == null) return true;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        return progress.isDone();
    }

    public static void grantSpellAdvancement(ServerPlayer player, SpellType type) {
        grantAdvancement(player, advancementIdFor(type));
    }

    public static void grantAdvancement(ServerPlayer player, Identifier advancementId) {
        net.minecraft.server.MinecraftServer server = ((net.minecraft.server.level.ServerLevel) player.level()).getServer();
        AdvancementHolder holder = server.getAdvancements().get(advancementId);
        if (holder == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }
    }

    public static void revokeSpellAdvancement(ServerPlayer player, SpellType type) {
        revokeAdvancement(player, advancementIdFor(type));
    }

    public static void revokeAdvancement(ServerPlayer player, Identifier advancementId) {
        net.minecraft.server.MinecraftServer server = ((net.minecraft.server.level.ServerLevel) player.level()).getServer();
        AdvancementHolder holder = server.getAdvancements().get(advancementId);
        if (holder == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getCompletedCriteria()) {
            player.getAdvancements().revoke(holder, criterion);
        }
    }
}
