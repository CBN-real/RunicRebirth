package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.DungeonPressurePlateBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DungeonPressurePlateBlock extends BaseEntityBlock {

    public static final MapCodec<DungeonPressurePlateBlock> CODEC = simpleCodec(DungeonPressurePlateBlock::new);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);
    private static final int RELEASE_TICKS = 20;

    public DungeonPressurePlateBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    public MapCodec<DungeonPressurePlateBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!level.isClientSide() && entity instanceof LivingEntity) {
            if (!state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, true), 3);
                level.updateNeighborsAt(pos, this);
                level.updateNeighborsAt(pos.below(), this);
                level.playSound(null, pos, ModSounds.DUNGEON_PRESSURE_PLATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            level.scheduleTick(pos, this, RELEASE_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            AABB area = new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.0625, pos.getZ() + 1);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
            if (entities.isEmpty()) {
                level.setBlock(pos, state.setValue(POWERED, false), 3);
                level.updateNeighborsAt(pos, this);
                level.updateNeighborsAt(pos.below(), this);
            } else {
                level.scheduleTick(pos, this, RELEASE_TICKS);
            }
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == Direction.UP && state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DungeonPressurePlateBlockEntity plate) {
                BlockState below = level.getBlockState(pos.below());
                plate.setMimickedState(below);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonPressurePlateBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return null;
    }
}
