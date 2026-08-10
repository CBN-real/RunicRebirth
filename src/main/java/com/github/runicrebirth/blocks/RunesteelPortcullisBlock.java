package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.RunesteelPortcullisBlockEntity;
import com.github.runicrebirth.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunesteelPortcullisBlock extends BaseEntityBlock {

    public static final MapCodec<RunesteelPortcullisBlock> CODEC = simpleCodec(RunesteelPortcullisBlock::new);

    public static final DirectionProperty FACING  = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty   OPEN    = BlockStateProperties.OPEN;
    /** True for the placed top block; false for invisible body blocks placed when closed. */
    public static final BooleanProperty   TOP     = BooleanProperty.create("top");
    /** True while the closing animation is playing (body blocks not yet placed). */
    public static final BooleanProperty   CLOSING = BooleanProperty.create("closing");
    /** 0 = open/idle. 1-9 = number of middle-bone segments (body blocks) the closed gate spans. */
    public static final IntegerProperty   HEIGHT  = IntegerProperty.create("height", 0, 9);

    private static final VoxelShape SHAPE_Z = box(0, 0, 7, 16, 16, 9);
    private static final VoxelShape SHAPE_X = box(7, 0, 0, 9, 16, 16);

    public RunesteelPortcullisBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, true)
            .setValue(TOP, true)
            .setValue(CLOSING, false)
            .setValue(HEIGHT, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, TOP, CLOSING, HEIGHT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(OPEN, true)
            .setValue(TOP, true)
            .setValue(CLOSING, false)
            .setValue(HEIGHT, 0);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(TOP) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        return handleGateUse(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide() && state.getValue(TOP)) {
            for (int i = 1; i <= 9; i++) {
                BlockPos below = pos.below(i);
                BlockState belowState = level.getBlockState(below);
                if (belowState.is(this) && !belowState.getValue(TOP)) {
                    level.removeBlock(below, false);
                } else {
                    break;
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** Fires after HEIGHT*20 ticks to place body blocks and finalize the closed state. */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CLOSING)) return;
        int height = state.getValue(HEIGHT);
        Direction facing = state.getValue(FACING);

        level.setBlock(pos, state.setValue(CLOSING, false), Block.UPDATE_ALL);

        for (int i = 1; i <= height; i++) {
            BlockPos bodyPos = pos.below(i);
            BlockState existing = level.getBlockState(bodyPos);
            if (existing.isAir() || existing.canBeReplaced()) {
                level.setBlock(bodyPos,
                    defaultBlockState()
                        .setValue(FACING, facing)
                        .setValue(OPEN, false)
                        .setValue(TOP, false)
                        .setValue(CLOSING, false)
                        .setValue(HEIGHT, height),
                    Block.UPDATE_ALL);
            }
        }
    }

    static InteractionResult handleGateUse(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.RUNESTEEL_PORTCULLIS.get())) return InteractionResult.PASS;

        if (!state.getValue(TOP)) {
            for (int i = 1; i <= 9; i++) {
                BlockPos above = pos.above(i);
                BlockState aboveState = level.getBlockState(above);
                if (aboveState.is(ModBlocks.RUNESTEEL_PORTCULLIS.get()) && aboveState.getValue(TOP)) {
                    return handleGateUse(level, above);
                }
            }
            return InteractionResult.PASS;
        }

        Direction facing = state.getValue(FACING);
        List<BlockPos> gate = findGate(level, pos, facing);
        if (gate.isEmpty()) return InteractionResult.PASS;

        if (state.getValue(OPEN)) {
            for (BlockPos gatePos : gate) {
                BlockState gateState = level.getBlockState(gatePos);
                if (!gateState.is(ModBlocks.RUNESTEEL_PORTCULLIS.get()) || !gateState.getValue(TOP)) continue;
                int height = calculateHeight(level, gatePos);
                if (height == 0) continue;
                level.setBlock(gatePos, gateState
                    .setValue(OPEN, false)
                    .setValue(CLOSING, true)
                    .setValue(HEIGHT, height), Block.UPDATE_ALL);
                ((ServerLevel) level).scheduleTick(gatePos, ModBlocks.RUNESTEEL_PORTCULLIS.get(), height * 20);
            }
        } else {
            for (BlockPos gatePos : gate) {
                BlockState gateState = level.getBlockState(gatePos);
                if (!gateState.is(ModBlocks.RUNESTEEL_PORTCULLIS.get()) || !gateState.getValue(TOP)) continue;
                int height = gateState.getValue(HEIGHT);
                for (int i = 1; i <= height; i++) {
                    BlockPos bodyPos = gatePos.below(i);
                    BlockState bodyState = level.getBlockState(bodyPos);
                    if (bodyState.is(ModBlocks.RUNESTEEL_PORTCULLIS.get()) && !bodyState.getValue(TOP)) {
                        level.removeBlock(bodyPos, false);
                    }
                }
                level.setBlock(gatePos, gateState
                    .setValue(OPEN, true)
                    .setValue(CLOSING, false)
                    .setValue(HEIGHT, 0), Block.UPDATE_ALL);
            }
        }

        return InteractionResult.SUCCESS;
    }

    /** Counts passable blocks directly below topPos, up to 9. */
    private static int calculateHeight(Level level, BlockPos topPos) {
        int count = 0;
        for (int i = 1; i <= 9; i++) {
            BlockState bs = level.getBlockState(topPos.below(i));
            if (bs.isAir() || bs.canBeReplaced()) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static List<BlockPos> findGate(Level level, BlockPos pos, Direction facing) {
        Direction.Axis axis = facing.getAxis();
        Direction horizontal = axis == Direction.Axis.Z ? Direction.EAST : Direction.NORTH;

        Set<BlockPos> visited = new LinkedHashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(pos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;
            for (Direction dir : new Direction[]{horizontal, horizontal.getOpposite()}) {
                BlockPos neighbor = current.relative(dir);
                if (!visited.contains(neighbor) && isSamePortcullis(level, neighbor, axis)) {
                    queue.add(neighbor);
                }
            }
        }

        return new ArrayList<>(visited);
    }

    static boolean isSamePortcullis(Level level, BlockPos pos, Direction.Axis axis) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.RUNESTEEL_PORTCULLIS.get())) return false;
        if (!state.getValue(TOP)) return false;
        return state.getValue(FACING).getAxis() == axis;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(TOP) ? new RunesteelPortcullisBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return null;
    }
}
