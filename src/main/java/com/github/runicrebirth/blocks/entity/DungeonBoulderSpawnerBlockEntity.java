package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.DungeonBoulderSpawnerBlock;
import com.github.runicrebirth.entities.DungeonBoulderEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class DungeonBoulderSpawnerBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SPAWN_BOULDER = RawAnimation.begin().thenLoop("spawn_boulder");

    // 200 ticks = 10s full cycle; plays spawn_boulder animation at tick 190 (0.5s before spawn)
    private static final int SPAWN_INTERVAL = 200;
    private static final int ANIM_WARN_TICK = 190;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int spawnTimer = 0;
    private AnimState animState = AnimState.IDLE;

    public enum AnimState { IDLE, SPAWN_BOULDER }

    public DungeonBoulderSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_BOULDER_SPAWNER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                   DungeonBoulderSpawnerBlockEntity be) {
        be.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        spawnTimer++;

        if (spawnTimer >= ANIM_WARN_TICK && animState == AnimState.IDLE) {
            animState = AnimState.SPAWN_BOULDER;
        }

        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            animState = AnimState.IDLE;
            if (level instanceof ServerLevel serverLevel) {
                spawnBoulder(serverLevel, pos, state);
            }
        }
    }

    private void spawnBoulder(ServerLevel level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(DungeonBoulderSpawnerBlock.FACING);

        // Spawn 1 block above center of spawner
        Vec3 spawnPos = Vec3.atCenterOf(pos).add(0, 0, 0);
        DungeonBoulderEntity boulder = DungeonBoulderEntity.create(level, facing);
        boulder.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        level.addFreshEntity(boulder);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DungeonBoulderSpawnerBlockEntity>("controller", 0, state ->
                switch (animState) {
                    case SPAWN_BOULDER -> state.setAndContinue(SPAWN_BOULDER);
                    default            -> state.setAndContinue(IDLE);
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
