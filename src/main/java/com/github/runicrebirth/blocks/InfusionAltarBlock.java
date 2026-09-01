package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.github.runicrebirth.blocks.multiblock.InfusionAltarValidator;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.items.SpellWriter;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class InfusionAltarBlock extends BaseEntityBlock {

    public static final MapCodec<InfusionAltarBlock> CODEC = simpleCodec(InfusionAltarBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
        box(0, 0, 0, 16, 35, 16)
    );

    public InfusionAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionAltarBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.INFUSION_ALTAR.get(), InfusionAltarBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof SpellWriter) {
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof InfusionAltarBlockEntity altar)) return InteractionResult.PASS;

        if (!stack.isEmpty()) {
            if (!altar.isActive() || altar.isCrafting() || altar.hasResult()) {
                return InteractionResult.PASS;
            }
            if (altar.getItemCount() >= InfusionAltarBlockEntity.MAX_ITEMS) {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Altar is full."), true);
                return InteractionResult.FAIL;
            }
            ItemStack toInsert = stack.copyWithCount(1);
            altar.addItem(toInsert);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof InfusionAltarBlockEntity altar)) return InteractionResult.PASS;

        if (altar.hasResult()) {
            ItemStack result = altar.removeResult();
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            return InteractionResult.SUCCESS;
        }

        if (altar.isCrafting()) {
            return InteractionResult.CONSUME;
        }

        if (altar.getItemCount() > 0) {
            ItemStack removed = altar.removeLastItem();
            if (!removed.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (altar.isActive() && altar.hasLastRecipe()) {
            if (altar.tryRepeatRecipe(player)) {
                return InteractionResult.SUCCESS;
            } else {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Missing ingredients."), true);
                return InteractionResult.CONSUME;
            }
        }

        boolean valid = InfusionAltarValidator.validate(level, pos);
        if (valid) {
            if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Infusion Altar structure found!"), true);
        } else {
            if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Infusion Altar structure incomplete."), true);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !state.is(oldState.getBlock())) {
            BlockPos above = pos.above();
            if (level.getBlockState(above).canBeReplaced()) {
                level.setBlock(above, ModBlocks.INFUSION_ALTAR_PROXY.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

}
