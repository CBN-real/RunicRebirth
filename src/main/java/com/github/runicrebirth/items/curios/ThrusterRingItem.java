package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.MagicItem;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class ThrusterRingItem extends MagicItem implements IActivatableRing {

    public static final Identifier COOLDOWN_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "thruster_ring");
    public static final Identifier DURATION_KEY =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "thruster_ring_duration");
    public static final int THRUST_DURATION_TICKS = 120;
    public static final int TOTAL_COOLDOWN_TICKS = 200;

    public ThrusterRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        MagicData data = MagicData.of(player);
        if (data.isOnCooldown(COOLDOWN_ID) || data.thrusterActiveTicks() > 0) return;

        data.setThrusterActiveTicks(THRUST_DURATION_TICKS);
        ((net.minecraft.server.level.ServerLevel) player.level()).playSound(null, player.getX(), player.getY(), player.getZ(),
            ModSounds.SPELLS_THRUSTER.get(), SoundSource.PLAYERS, 0.25f, 1.0f);
    }
}
