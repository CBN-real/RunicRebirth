package com.github.runicrebirth.api.magic;

import com.github.runicrebirth.capabilities.magic.MagicData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public final class MagicHelper {

    private MagicHelper() {}

    public static MagicData getMagicData(Player player) {
        return MagicData.of(player);
    }

    public static boolean isOnCooldown(Player player, Identifier spellId) {
        return MagicData.of(player).isOnCooldown(spellId);
    }

    public static boolean canCast(Player player, Identifier spellId) {
        MagicData data = MagicData.of(player);
        return !data.isOnCooldown(spellId) && data.globalCastLockoutTicks() <= 0;
    }
}
