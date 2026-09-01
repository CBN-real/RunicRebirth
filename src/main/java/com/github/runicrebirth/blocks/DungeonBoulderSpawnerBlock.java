package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.DungeonBoulderSpawnerBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// Renders as 3×3×1 via GeckoLib; collision is standard 1×1×1
public class DungeonBoulderSpawnerBlock extends BaseEntityBlock {

    public static final MapCodec<DungeonBoulderSpawnerBlock> CODEC = simpleCodec(DungeonBoulderSpawnerBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public DungeonBoulderSpawnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public VoxelShape makeShape(){
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(-1, 0.75, -1, -0.75, 1, 2), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(1.75, 0.75, -1, 2, 1, 2), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-1, 0.75, 1.75, 2, 1, 2), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-1, 0.75, -1, 2, 1, -0.75), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-1.0625, 0.6875, -1.0625, -0.6875, 1.0625, -0.6875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(1.6875, 0.6875, -1.0625, 2.0625, 1.0625, -0.6875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(1.6875, 0.6875, 1.6875, 2.0625, 1.0625, 2.0625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-1.0625, 0.6875, 1.6875, -0.6875, 1.0625, 2.0625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-0.6499999999999999, 0.875, -0.6499999999999999, 1.65, 0.875, 1.65), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(1.25, 0.8125, -1.0625, 1.4375, 0.9375, 0.25), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-0.4375, 0.8125, -1.0625, -0.25, 0.9375, 0.25), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-0.4375, 0.8125, 0.75, -0.25, 0.9375, 2.0625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(1.25, 0.8125, 0.75, 1.4375, 0.9375, 2.0625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(-0.9375, 1, -0.9375, 1.9375, 1, 1.9375), BooleanOp.OR);

      return shape;
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
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonBoulderSpawnerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return makeShape();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type,
                ModBlockEntities.DUNGEON_BOULDER_SPAWNER.get(),
                DungeonBoulderSpawnerBlockEntity::serverTick);
    }
}
