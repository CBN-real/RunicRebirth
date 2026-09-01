package com.github.runicrebirth.items;

import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SectBannerVariantItem extends RunicBlockItem {

    public SectBannerVariantItem(Block block, Properties props) {
        super(block, props, "idle", ((SectBannerVariantBlock) block).getTextureName());
        SectBannerVariantBlock vb = (SectBannerVariantBlock) block;
        String geoName = vb.getBannerType() == SectBannerVariantBlock.BannerType.TATTERED
                ? "tattered_sect_banner" : "sect_banner";
        withGeoPath("block/" + geoName);
        withAnimationPath("block/" + geoName);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player,
                                                 ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SectBannerVariantBlockEntity be) {
            BannerPatternLayers patterns = stack.getOrDefault(DataComponents.BANNER_PATTERNS,
                    BannerPatternLayers.EMPTY);
            be.setPatterns(patterns);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
        return false;
    }
}
