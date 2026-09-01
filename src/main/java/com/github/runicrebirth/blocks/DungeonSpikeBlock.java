package com.github.runicrebirth.blocks;

import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.init.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DungeonSpikeBlock extends Block {

    public static final MapCodec<DungeonSpikeBlock> CODEC = simpleCodec(DungeonSpikeBlock::new);

    private static final float DAMAGE_PER_SECOND = 5.0f;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public DungeonSpikeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<DungeonSpikeBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            entity.setDeltaMovement(
                entity.getDeltaMovement().multiply(0.4, 0.05, 0.4)
            );

            if (entity.tickCount % 20 == 0) {
                DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPosition(pos);
                float sharpMult = inst != null ? inst.getSharpTrapMultiplier() : 1.0f;
                living.hurt(level.damageSources().generic(), DAMAGE_PER_SECOND * sharpMult);
                level.playSound(null, pos, ModSounds.DUNGEON_SPIKE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, net.minecraft.core.Direction side) {
        return false;
    }
}
