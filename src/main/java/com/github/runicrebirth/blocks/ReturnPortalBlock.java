package com.github.runicrebirth.blocks;

import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ReturnPortalBlock extends Block {

    public ReturnPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DungeonInstance instance = DungeonInstanceManager.get().getInstanceForPlayer(serverPlayer.getUUID());
            if (instance != null) {
                DungeonTeleporter.teleportFromDungeon(serverPlayer, instance);
            } else {
                player.displayClientMessage(Component.literal("No active dungeon instance."), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
