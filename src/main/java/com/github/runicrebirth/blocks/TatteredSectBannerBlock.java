package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.TatteredSectBannerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TatteredSectBannerBlock extends AbstractSectBannerBlock {

    public static final MapCodec<TatteredSectBannerBlock> CODEC = simpleCodec(TatteredSectBannerBlock::new);

    public TatteredSectBannerBlock(Properties props) { super(props); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TatteredSectBannerBlockEntity(pos, state);
    }
}
