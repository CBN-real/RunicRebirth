package com.github.runicrebirth.blocks;

import com.github.runicrebirth.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InfusionAltarProxyBlock extends Block {

    public static final MapCodec<InfusionAltarProxyBlock> CODEC = simpleCodec(InfusionAltarProxyBlock::new);

    private static final VoxelShape SHAPE = box(1, 14, 1, 15, 15, 15);

    public InfusionAltarProxyBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static final VoxelShape COLLISION_SHAPE = box(0, 0, 0, 16, 19, 16);

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos altarPos = pos.below();
        BlockState altarState = level.getBlockState(altarPos);
        if (altarState.getBlock() instanceof InfusionAltarBlock) {
            BlockHitResult forwarded = new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), altarPos, hitResult.isInside());
            return altarState.useItemOn(stack, level, player, hand, forwarded);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        BlockPos altarPos = pos.below();
        BlockState altarState = level.getBlockState(altarPos);
        if (altarState.getBlock() instanceof InfusionAltarBlock) {
            BlockHitResult forwarded = new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), altarPos, hitResult.isInside());
            return altarState.useWithoutItem(level, player, forwarded);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockPos altarPos = pos.below();
        if (level.getBlockState(altarPos).is(ModBlocks.INFUSION_ALTAR.get())) {
            level.destroyBlock(altarPos, true);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}
