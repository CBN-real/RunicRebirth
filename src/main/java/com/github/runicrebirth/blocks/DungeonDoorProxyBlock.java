package com.github.runicrebirth.blocks;

import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.redstone.Orientation;

public class DungeonDoorProxyBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty   OPEN   = BlockStateProperties.OPEN;

    private static final VoxelShape SHAPE_Z = box(0, 0, 4, 16, 16, 12);
    private static final VoxelShape SHAPE_X = box(4, 0, 0, 12, 16, 16);

    public DungeonDoorProxyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) return Shapes.empty();
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        if (level.isClientSide()) return;
        notifyController(level, pos, state);
    }

    static void notifyController(Level level, BlockPos proxyPos, BlockState proxyState) {
        Direction facing = proxyState.getValue(FACING);
        boolean xAxis = facing.getAxis() == Direction.Axis.X;
        for (int dy = 0; dy >= -3; dy--) {
            for (int dl = -2; dl <= 2; dl++) {
                BlockPos check = xAxis ? proxyPos.offset(0, dy, dl) : proxyPos.offset(dl, dy, 0);
                BlockState checkState = level.getBlockState(check);
                if (checkState.is(ModBlocks.DUNGEON_DOOR.get())) {
                    DungeonDoorBlock.checkRedstone(level, check, checkState);
                    return;
                }
            }
        }
    }
}
