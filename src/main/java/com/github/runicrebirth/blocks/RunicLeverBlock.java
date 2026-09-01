package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.RunicLeverBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RunicLeverBlock extends BaseEntityBlock {

    public static final MapCodec<RunicLeverBlock> CODEC = simpleCodec(RunicLeverBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape FLOOR   = Block.box(4,  0,  4, 12,  8, 12);
    private static final VoxelShape CEILING = Block.box(4,  8,  4, 12, 16, 12);
    private static final VoxelShape WALL_N  = Block.box(4,  4,  8, 12, 12, 16);
    private static final VoxelShape WALL_S  = Block.box(4,  4,  0, 12, 12,  8);
    private static final VoxelShape WALL_E  = Block.box(0,  4,  4,  8, 12, 12);
    private static final VoxelShape WALL_W  = Block.box(8,  4,  4, 16, 12, 12);

    public RunicLeverBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACE, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();
        return switch (clicked) {
            case UP   -> defaultBlockState().setValue(FACE, AttachFace.FLOOR).setValue(FACING, horizontal);
            case DOWN -> defaultBlockState().setValue(FACE, AttachFace.CEILING).setValue(FACING, horizontal);
            default   -> defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, clicked);
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
            case CEILING -> level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
            case WALL    -> level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()))
                                 .isFaceSturdy(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> FLOOR;
            case CEILING -> CEILING;
            case WALL    -> switch (state.getValue(FACING)) {
                case NORTH -> WALL_N;
                case SOUTH -> WALL_S;
                case EAST  -> WALL_E;
                case WEST  -> WALL_W;
                default    -> FLOOR;
            };
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicLeverBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            boolean nowPowered = !state.getValue(POWERED);
            level.setBlock(pos, state.setValue(POWERED, nowPowered), Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, this);
            if (level.getBlockEntity(pos) instanceof RunicLeverBlockEntity be) {
                be.setPowered(nowPowered);
            }
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS,
                    0.3f, nowPowered ? 0.6f : 0.5f);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }
}
