package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SectBannerBlock extends AbstractSectBannerBlock {

    public static final MapCodec<SectBannerBlock> CODEC = simpleCodec(SectBannerBlock::new);

    public SectBannerBlock(Properties props) { super(props); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SectBannerBlockEntity(pos, state);
    }
}
