package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.network.PhantomMiningSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RingOfPhantomMiningItem extends MagicItem implements IActivatableRing {

    public static final int EFFECT_TICKS = 1200;
    public static final ResourceLocation DURATION_KEY =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "phantom_mining_ring_duration");

    public RingOfPhantomMiningItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        MagicData data = MagicData.of(player);
        int newTicks = data.phantomMiningTicks() > 0 ? 0 : EFFECT_TICKS;
        data.setPhantomMiningTicks(newTicks);
        PhantomMiningSyncS2CPacket.sendTo(player, newTicks);
    }
}
