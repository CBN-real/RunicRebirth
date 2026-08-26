package com.github.runicrebirth.blocks;

import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class AdeptStatueProxyBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AdeptStatueProxyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) return;
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        boolean isBase = belowState.is(ModBlocks.ADEPT_MAGE_STATUE.get())
                || belowState.is(ModBlocks.ADEPT_WIZARD_STATUE.get())
                || belowState.is(ModBlocks.ADEPT_RUNEBLADE_STATUE.get())
                || belowState.is(ModBlocks.ADEPT_ARTIFICER_STATUE.get());
        if (!isBase) {
            level.removeBlock(pos, false);
        }
    }
}
