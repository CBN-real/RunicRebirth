package com.github.runicrebirth.advancement;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellType;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SpellAdvancementHelper {

    private SpellAdvancementHelper() {}

    public static ResourceLocation advancementIdFor(SpellType type) {
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells/" + type.id().getPath());
    }

    public static ResourceLocation advancementIdFor(ResourceLocation spellTypeId) {
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells/" + spellTypeId.getPath());
    }

    public static boolean hasSpellUnlocked(ServerPlayer player, SpellType type) {
        return hasAdvancement(player, advancementIdFor(type));
    }

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder holder = player.server.getAdvancements().get(advancementId);
        if (holder == null) return true;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        return progress.isDone();
    }

    public static void grantSpellAdvancement(ServerPlayer player, SpellType type) {
        grantAdvancement(player, advancementIdFor(type));
    }

    public static void grantAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder holder = player.server.getAdvancements().get(advancementId);
        if (holder == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }
    }

    public static void revokeSpellAdvancement(ServerPlayer player, SpellType type) {
        revokeAdvancement(player, advancementIdFor(type));
    }

    public static void revokeAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder holder = player.server.getAdvancements().get(advancementId);
        if (holder == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getCompletedCriteria()) {
            player.getAdvancements().revoke(holder, criterion);
        }
    }
}
