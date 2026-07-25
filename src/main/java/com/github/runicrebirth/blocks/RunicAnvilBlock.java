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
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RunicAnvilBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
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
        return RenderShape.ENTITYBLOCK_ANIMATED;
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (stack.getItem() instanceof SpellWriter && !((RunicAnvilBlockEntity) be).isActive()) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (!(be instanceof RunicAnvilBlockEntity anvil)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;



        if (!stack.isEmpty()) {
            if (!anvil.isActive() || anvil.isCrafting() || anvil.hasResult()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (anvil.getItemCount() >= RunicAnvilBlockEntity.MAX_ITEMS) {
                player.displayClientMessage(Component.literal("Anvil is full."), true);
                return ItemInteractionResult.FAIL;
            }
            ItemStack toInsert = stack.copyWithCount(1);
            anvil.addItem(toInsert);
            stack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.sidedSuccess(true);

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
            return InteractionResult.sidedSuccess(false);
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
                player.displayClientMessage(Component.literal("Missing ingredients."), true);
                return InteractionResult.sidedSuccess(false);
            }
        }

        boolean valid = RunicAnvilValidator.validate(level, pos);
        if (valid) {
            player.displayClientMessage(Component.literal("Runic Anvil structure found!"), true);
        } else {
            player.displayClientMessage(Component.literal("Runic Anvil structure incomplete."), true);
        }

        return InteractionResult.sidedSuccess(false);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RunicAnvilBlockEntity anvil) {
                anvil.deactivate();
                anvil.dropAllItems();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
