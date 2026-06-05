package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RunicCodexItem extends Item {

    public static final ResourceLocation BOOK_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_codex");

    public RunicCodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            var book = BookDataManager.get().getBook(BOOK_ID);
            if (book != null) {
                BookGuiManager.get().openBook(BookAddress.defaultFor(BOOK_ID));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
