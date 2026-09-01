package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.items.MagicItem;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class HoverRingItem extends MagicItem implements IActivatableRing {

    public static final Identifier DURATION_KEY =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hover_ring_duration");

    public HoverRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        MagicData data = MagicData.of(player);
        data.setGlidingActive(!data.isGlidingActive());
    }
}
