package com.github.interactivemagic.api.magic;

import com.github.interactivemagic.capabilities.magic.MagicData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class MagicHelper {

    private MagicHelper() {}

    public static MagicData getMagicData(Player player) {
        return MagicData.of(player);
    }

    public static boolean isOnCooldown(Player player, ResourceLocation spellId) {
        return MagicData.of(player).isOnCooldown(spellId);
    }

    public static boolean canCast(Player player, ResourceLocation spellId) {
        MagicData data = MagicData.of(player);
        return !data.isOnCooldown(spellId) && data.globalCastLockoutTicks() <= 0;
    }
}
