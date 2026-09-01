package com.github.runicrebirth.items;

import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.blocks.DungeonMobSpawnerBlock;
import com.github.runicrebirth.blocks.DungeonRoomTrackerBlock;
import com.github.runicrebirth.blocks.entity.DungeonRoomTrackerBlockEntity;
import com.github.runicrebirth.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DungeonRoomConfiguratorItem extends Item {

    public DungeonRoomConfiguratorItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos clickedPos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        Block clicked = level.getBlockState(clickedPos).getBlock();

        if (player.isShiftKeyDown() && clicked instanceof DungeonRoomTrackerBlock) {
            if (!level.isClientSide()) {
                stack.set(ModDataComponents.CONFIGURATOR_TRACKER_POS.get(), clickedPos);
                player.sendOverlayMessage(Component.literal(
                    "Selected Dungeon Room Tracker at " + posStr(clickedPos)));
            }
            return InteractionResult.SUCCESS;
        }

        BlockPos trackerPos = stack.get(ModDataComponents.CONFIGURATOR_TRACKER_POS.get());
        if (trackerPos == null) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(
                    Component.literal("Shift-right-click a Dungeon Room Tracker to select it first!"));
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(trackerPos);
            if (!(be instanceof DungeonRoomTrackerBlockEntity tracker)) {
                player.sendOverlayMessage(Component.literal("Selected tracker not found!"));
                return InteractionResult.CONSUME;
            }

            if (clicked instanceof DungeonDoorBlock) {
                if (!tracker.getDoorPositions().contains(clickedPos)) {
                    tracker.getDoorPositions().add(clickedPos);
                    tracker.setChanged();
                }
                player.sendOverlayMessage(Component.literal("Added door at " + posStr(clickedPos)));
            } else if (clicked instanceof DungeonMobSpawnerBlock) {
                if (!tracker.getSpawnerPositions().contains(clickedPos)) {
                    tracker.getSpawnerPositions().add(clickedPos);
                    tracker.setChanged();
                }
                player.sendOverlayMessage(Component.literal("Added spawner at " + posStr(clickedPos)));
            } else {
                if (!tracker.getActivationBlockPositions().contains(clickedPos)) {
                    tracker.getActivationBlockPositions().add(clickedPos);
                    tracker.setChanged();
                }
                player.sendOverlayMessage(Component.literal("Added activation block at " + posStr(clickedPos)));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private String posStr(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
