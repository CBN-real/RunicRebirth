package com.github.runicrebirth.items;

import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SectBannerItem extends RunicBlockItem {

    public SectBannerItem(Block block, Properties props) {
        super(block, props, "idle", "sect_banner");
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player,
                                                 ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SectBannerBlockEntity be) {
            DyeColor color = stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE);
            BannerPatternLayers patterns = stack.getOrDefault(DataComponents.BANNER_PATTERNS,
                    BannerPatternLayers.EMPTY);
            be.setBaseColor(color);
            be.setPatterns(patterns);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
        return false;
    }
}
