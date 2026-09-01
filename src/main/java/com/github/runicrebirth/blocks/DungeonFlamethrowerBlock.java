package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.DungeonFlamethrowerBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// Small 0.25×0.25×0.25 device, placeable on any face. Fires FIRE_ELEMENT flame cone.
public class DungeonFlamethrowerBlock extends BaseEntityBlock {

    public static final MapCodec<DungeonFlamethrowerBlock> CODEC = simpleCodec(DungeonFlamethrowerBlock::new);
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    // 4×4×4 pixel cube (0.25 blocks), positioned flush against the face it's attached to
    private static final VoxelShape FLOOR   = Block.box(6, 0, 6, 10, 4, 10);
    private static final VoxelShape CEILING = Block.box(6, 12, 6, 10, 16, 10);
    private static final VoxelShape WALL_N  = Block.box(6, 6, 12, 10, 10, 16);
    private static final VoxelShape WALL_S  = Block.box(6, 6,  0, 10, 10,  4);
    private static final VoxelShape WALL_E  = Block.box(0, 6,  6,  4, 10, 10);
    private static final VoxelShape WALL_W  = Block.box(12, 6, 6, 16, 10, 10);

    public DungeonFlamethrowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();
        return switch (clickedFace) {
            case UP   -> defaultBlockState().setValue(FACE, AttachFace.FLOOR).setValue(FACING, horizontal);
            case DOWN -> defaultBlockState().setValue(FACE, AttachFace.CEILING).setValue(FACING, horizontal);
            default   -> defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, clickedFace);
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

    public static Direction getFireDirection(BlockState state) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL    -> state.getValue(FACING);
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonFlamethrowerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type,
                ModBlockEntities.DUNGEON_FLAMETHROWER.get(),
                DungeonFlamethrowerBlockEntity::serverTick);
    }
}
