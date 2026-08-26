package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.MeditationCushionBlockEntity;
import com.github.runicrebirth.blocks.multiblock.EarthVeinValidator;
import com.github.runicrebirth.entities.SeatEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeditationCushionBlock extends BaseEntityBlock {

    public static final MapCodec<MeditationCushionBlock> CODEC = simpleCodec(MeditationCushionBlock::new);

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 4, 15);

    public MeditationCushionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MeditationCushionBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.MEDITATION_CUSHION.get(), MeditationCushionBlockEntity::tick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            boolean valid = EarthVeinValidator.validate(level, pos);
            if (valid) {
                player.displayClientMessage(Component.literal("Earth Vein structure is complete!"), true);
            } else {
                player.displayClientMessage(Component.literal("Earth Vein structure is incomplete."), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.getVehicle() instanceof SeatEntity seat && pos.equals(seat.getHomePos())) {
            player.stopRiding();
            return InteractionResult.SUCCESS;
        }

        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class,
                new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));

        SeatEntity seat;
        if (!seats.isEmpty()) {
            seat = seats.get(0);
            if (!seat.getPassengers().isEmpty()) return InteractionResult.FAIL;
        } else {
            seat = new SeatEntity(level, pos);
            level.addFreshEntity(seat);
        }

        player.startRiding(seat, true);

        if (player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeditationCushionBlockEntity cushionBE && cushionBE.isActive()) {
                cushionBE.onPlayerSit(serverPlayer);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            level.getEntitiesOfClass(SeatEntity.class,
                    new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1))
                    .forEach(s -> { s.ejectPassengers(); s.discard(); });
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
