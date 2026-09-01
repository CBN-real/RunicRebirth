package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.DungeonRoomTrackerBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.network.OpenDungeonRoomTrackerS2CPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class DungeonRoomTrackerBlock extends BaseEntityBlock {

    public static final MapCodec<DungeonRoomTrackerBlock> CODEC = simpleCodec(DungeonRoomTrackerBlock::new);

    public DungeonRoomTrackerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonRoomTrackerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer && player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DungeonRoomTrackerBlockEntity tracker) {
                PacketDistributor.sendToPlayer(serverPlayer,
                    new OpenDungeonRoomTrackerS2CPacket(pos, tracker.serializeConfig()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.DUNGEON_ROOM_TRACKER.get(), DungeonRoomTrackerBlockEntity::serverTick);
    }
}
