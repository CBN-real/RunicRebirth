package com.github.runicrebirth.blocks;

import com.github.runicrebirth.init.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DungeonTemporaryPlatformBlock extends Block {

    public static final MapCodec<DungeonTemporaryPlatformBlock> CODEC = simpleCodec(DungeonTemporaryPlatformBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final int STAND_TICKS = 25;
    private static final int GONE_TICKS = 100;

    public DungeonTemporaryPlatformBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    public MapCodec<DungeonTemporaryPlatformBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && state.getValue(ACTIVE)) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, STAND_TICKS);
                level.playSound(null, pos, ModSounds.DUNGEON_TEMPORARY_PLATFORM.get(), SoundSource.BLOCKS, 0.3f, 1.3f);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
            level.scheduleTick(pos, this, GONE_TICKS);
        } else {
            level.setBlock(pos, state.setValue(ACTIVE, true), 3);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(ACTIVE) ? super.getCollisionShape(state, level, pos, context) : Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(ACTIVE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }
}
