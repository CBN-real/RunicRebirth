package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.DungeonDoorBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DungeonDoorBlock extends BaseEntityBlock {

    public static final MapCodec<DungeonDoorBlock> CODEC = simpleCodec(DungeonDoorBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty   OPEN   = BlockStateProperties.OPEN;

    private static final VoxelShape SHAPE_Z = box(0, 0, 4, 16, 16, 12);
    private static final VoxelShape SHAPE_X = box(4, 0, 0, 12, 16, 16);

    public DungeonDoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(OPEN, false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonDoorBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        //if (state.getValue(OPEN)) return Shapes.empty();
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) return Shapes.empty();
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        checkRedstone(level, pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        fillProxyBlocks((ServerLevel) level, pos, state.getValue(FACING), state.getValue(OPEN));
        checkRedstone(level, pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            removeProxyBlocks((ServerLevel) level, pos, state.getValue(FACING));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.DUNGEON_DOOR.get(), DungeonDoorBlockEntity::tick);
    }

    static void checkRedstone(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        Direction facing = state.getValue(FACING);
        boolean powered = isAnyPositionPowered(level, pos, facing);
        boolean open = state.getValue(OPEN);
        if (powered != open) {
            setOpen((ServerLevel) level, pos, state, powered);
        }
    }

    private static boolean isAnyPositionPowered(Level level, BlockPos controllerPos, Direction facing) {
        if (level.hasNeighborSignal(controllerPos)) return true;
        for (BlockPos offset : getProxyOffsets(facing)) {
            if (level.hasNeighborSignal(controllerPos.offset(offset))) return true;
        }
        return false;
    }

    public static void setOpen(ServerLevel level, BlockPos pos, BlockState state, boolean open) {
        BlockState newState = state.setValue(OPEN, open);
        // UPDATE_CLIENTS only — UPDATE_ALL notifies neighbors which triggers checkRedstone via proxies,
        // causing the door to close again immediately when opened without a redstone signal.
        level.setBlock(pos, newState, Block.UPDATE_CLIENTS);

        Direction facing = state.getValue(FACING);
        for (BlockPos offset : getProxyOffsets(facing)) {
            BlockPos target = pos.offset(offset);
            BlockState targetState = level.getBlockState(target);
            if (targetState.is(ModBlocks.DUNGEON_DOOR_PROXY.get())) {
                level.setBlock(target, targetState.setValue(DungeonDoorProxyBlock.OPEN, open), Block.UPDATE_CLIENTS);
            }
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DungeonDoorBlockEntity doorBE) {
            doorBE.setAnimatingOpen(open);
        }
    }

    private static void fillProxyBlocks(ServerLevel level, BlockPos controllerPos, Direction facing, boolean open) {
        BlockState proxyState = ModBlocks.DUNGEON_DOOR_PROXY.get().defaultBlockState()
            .setValue(DungeonDoorProxyBlock.FACING, facing)
            .setValue(DungeonDoorProxyBlock.OPEN, open);
        for (BlockPos offset : getProxyOffsets(facing)) {
            BlockPos target = controllerPos.offset(offset);
            BlockState existing = level.getBlockState(target);
            if (existing.isAir() || existing.canBeReplaced()) {
                level.setBlock(target, proxyState, Block.UPDATE_ALL);
            }
        }
    }

    static void removeProxyBlocks(ServerLevel level, BlockPos controllerPos, Direction facing) {
        for (BlockPos offset : getProxyOffsets(facing)) {
            BlockPos target = controllerPos.offset(offset);
            if (level.getBlockState(target).is(ModBlocks.DUNGEON_DOOR_PROXY.get())) {
                level.removeBlock(target, false);
            }
        }
    }

    static List<BlockPos> getProxyOffsets(Direction facing) {
        boolean xAxis = facing.getAxis() == Direction.Axis.X;
        List<BlockPos> offsets = new ArrayList<>();
        if (xAxis) {
            offsets.add(new BlockPos(0, 0, -1));
            offsets.add(new BlockPos(0, 0, 1));
        } else {
            offsets.add(new BlockPos(-1, 0, 0));
            offsets.add(new BlockPos(1, 0, 0));
        }
        for (int y = 1; y <= 3; y++) {
            offsets.add(new BlockPos(0, y, 0));
            if (xAxis) {
                offsets.add(new BlockPos(0, y, -1));
                offsets.add(new BlockPos(0, y, 1));
            } else {
                offsets.add(new BlockPos(-1, y, 0));
                offsets.add(new BlockPos(1, y, 0));
            }
        }
        return offsets;
    }
}
