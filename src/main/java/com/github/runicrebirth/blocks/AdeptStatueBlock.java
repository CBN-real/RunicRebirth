package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.AdeptStatueBlockEntity;
import com.github.runicrebirth.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class AdeptStatueBlock extends BaseEntityBlock {

    public static final MapCodec<AdeptStatueBlock> CODEC = simpleCodec(AdeptStatueBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AdeptStatueBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos above = context.getClickedPos().above();
        BlockState aboveState = context.getLevel().getBlockState(above);
        if (!aboveState.isAir() && !aboveState.canBeReplaced()) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide()) return;
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.isAir() || aboveState.canBeReplaced()) {
            level.setBlock(above, ModBlocks.ADEPT_STATUE_PROXY.get().defaultBlockState()
                    .setValue(AdeptStatueProxyBlock.FACING, state.getValue(FACING)), Block.UPDATE_ALL);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).is(ModBlocks.ADEPT_STATUE_PROXY.get())) {
                level.removeBlock(above, false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdeptStatueBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
