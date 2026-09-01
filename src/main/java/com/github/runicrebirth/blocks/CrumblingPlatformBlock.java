package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.CrumblingPlatformBlockEntity;
import com.github.runicrebirth.entities.CrumblingPlatformFallingEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class CrumblingPlatformBlock extends BaseEntityBlock {

    public static final MapCodec<CrumblingPlatformBlock> CODEC = simpleCodec(CrumblingPlatformBlock::new);
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final EnumProperty<Direction> PLACED_ON_FACE = EnumProperty.create("placed_on_face", Direction.class);

    // 0.25s each phase at 20 ticks/s
    private static final int CASCADE_TICKS = 5;
    private static final int FALL_TICKS = 5;

    public CrumblingPlatformBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(TRIGGERED, false)
                .setValue(POWERED, false)
                .setValue(PLACED_ON_FACE, Direction.DOWN));
    }

    @Override
    public MapCodec<CrumblingPlatformBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED, POWERED, PLACED_ON_FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PLACED_ON_FACE, context.getClickedFace());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        if (!level.isClientSide() && !state.getValue(TRIGGERED) && level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.setValue(TRIGGERED, true), 3);
            level.scheduleTick(pos, this, CASCADE_TICKS);
            level.playSound(null, pos, ModSounds.DUNGEON_CRUMBLING_PLATFORM.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(TRIGGERED) && !state.getValue(POWERED)) {
            // Phase 1: emit redstone for cascade to adjacent crumbling platforms
            BlockState newState = state.setValue(POWERED, true);
            level.setBlock(pos, newState, 3);
            level.updateNeighborsAt(pos, this);
            level.scheduleTick(pos, this, FALL_TICKS);
        } else if (state.getValue(TRIGGERED) && state.getValue(POWERED)) {
            // Phase 2: remove block and spawn falling entity
            BlockState mimickedState = Blocks.STONE.defaultBlockState();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CrumblingPlatformBlockEntity platform) {
                mimickedState = platform.getMimickedState();
            }
            BlockState restoreState = state.setValue(TRIGGERED, false).setValue(POWERED, false);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            CrumblingPlatformFallingEntity entity = new CrumblingPlatformFallingEntity(
                    ModEntities.CRUMBLING_PLATFORM_FALLING.get(), level);
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            entity.setFallingState(mimickedState);
            entity.setRespawnData(pos, restoreState);
            level.addFreshEntity(entity);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CrumblingPlatformBlockEntity platform) {
                Direction face = state.getValue(PLACED_ON_FACE);
                BlockPos placedOnPos = pos.relative(face.getOpposite());
                BlockState placedOnState = level.getBlockState(placedOnPos);
                BlockState toMimic;
                if (placedOnState.getBlock() instanceof CrumblingPlatformBlock) {
                    BlockEntity neighborBe = level.getBlockEntity(placedOnPos);
                    toMimic = neighborBe instanceof CrumblingPlatformBlockEntity nbp
                            ? nbp.getMimickedState()
                            : Blocks.STONE.defaultBlockState();
                } else {
                    toMimic = placedOnState;
                }
                platform.setMimickedState(toMimic);
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrumblingPlatformBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return null;
    }
}
