package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.github.runicrebirth.blocks.multiblock.RunicAnvilValidator;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.items.SpellWriter;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RunicAnvilBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<RunicAnvilBlock> CODEC = simpleCodec(RunicAnvilBlock::new);

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    public RunicAnvilBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicAnvilBlockEntity(pos, state);
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
        return createTickerHelper(type, ModBlockEntities.RUNIC_ANVIL.get(), RunicAnvilBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (stack.getItem() instanceof SpellWriter && !((RunicAnvilBlockEntity) be).isActive()) {
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (!(be instanceof RunicAnvilBlockEntity anvil)) return InteractionResult.PASS;



        if (!stack.isEmpty()) {
            if (!anvil.isActive() || anvil.isCrafting() || anvil.hasResult()) {
                return InteractionResult.PASS;
            }
            if (anvil.getItemCount() >= RunicAnvilBlockEntity.MAX_ITEMS) {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Anvil is full."), true);
                return InteractionResult.FAIL;
            }
            ItemStack toInsert = stack.copyWithCount(1);
            anvil.addItem(toInsert);
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
        if (!(be instanceof RunicAnvilBlockEntity anvil)) return InteractionResult.PASS;

        if (anvil.hasResult()) {
            ItemStack result = anvil.removeResult();
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            return InteractionResult.SUCCESS;
        }

        if (anvil.isCrafting()) {
            return InteractionResult.CONSUME;
        }

        if (anvil.getItemCount() > 0) {
            ItemStack removed = anvil.removeLastItem();
            if (!removed.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (anvil.isActive() && anvil.hasLastRecipe()) {
            if (anvil.tryRepeatRecipe(player)) {
                return InteractionResult.SUCCESS;
            } else {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("Missing ingredients."), true);
                return InteractionResult.CONSUME;
            }
        }

        boolean valid = RunicAnvilValidator.validate(level, pos);
        if (player instanceof ServerPlayer sp) {
            if (valid) {
                sp.sendSystemMessage(Component.literal("Runic Anvil structure found!"), true);
            } else {
                sp.sendSystemMessage(Component.literal("Runic Anvil structure incomplete."), true);
            }
        }

        return InteractionResult.CONSUME;
    }

}
