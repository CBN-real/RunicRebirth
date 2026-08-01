package com.github.runicrebirth.items;

import com.github.runicrebirth.menu.RunicKeyRingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RunicKeyRingItem extends Item {

    public static final int SIZE = 10;

    public RunicKeyRingItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            sp.openMenu(new SimpleMenuProvider(
                (windowId, inv, p) -> new RunicKeyRingMenu(windowId, inv, slot),
                Component.translatable("container.runicrebirth.runic_key_ring")
            ), buf -> buf.writeVarInt(slot));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
