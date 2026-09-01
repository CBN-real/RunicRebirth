package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.DungeonFlamethrowerBlock;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DungeonFlamethrowerBlockEntity extends BlockEntity {

    // 100-tick cycle: 20 ticks firing (1s), 80 ticks cooldown (4s)
    private static final int CYCLE_LENGTH = 100;
    private static final int FIRE_DURATION = 20;
    private static final float MAGIC_DAMAGE_PER_TICK = 5.0f; // 2.5 hearts
    private static final double CONE_RANGE = 3.0;
    private static final double CONE_HALF_ANGLE_COS = Math.cos(Math.toRadians(30)); // 60° cone
    private static final int FIRE_TICKS = 60;

    private int cycleTimer = 0;

    public DungeonFlamethrowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_FLAMETHROWER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                   DungeonFlamethrowerBlockEntity be) {
        be.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        cycleTimer = (cycleTimer + 1) % CYCLE_LENGTH;

        boolean firing = cycleTimer < FIRE_DURATION;
        if (!firing) return;

        Direction fireDir = DungeonFlamethrowerBlock.getFireDirection(state);
        Vec3 fireVec = Vec3.atLowerCornerOf(fireDir.getUnitVec3i());
        // Nozzle face is 0.25 blocks back from center (model is 4px cube flush against its wall)
        Vec3 nozzlePos = Vec3.atCenterOf(pos).subtract(fireVec.scale(0.4));

        DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPosition(pos);
        float fireMult = inst != null ? inst.getFireTrapMultiplier() : 1.0f;

        // Damage entities within 60° cone
        AABB searchBox = AABB.ofSize(nozzlePos.add(fireVec.scale(CONE_RANGE / 2.0)),
                CONE_RANGE * 2, CONE_RANGE * 2, CONE_RANGE * 2);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox);
        for (LivingEntity target : targets) {
            // Use closest point on AABB to nozzle — handles entities pressed against wall
            AABB bb = target.getBoundingBox();
            double cx = Math.max(bb.minX, Math.min(nozzlePos.x, bb.maxX));
            double cy = Math.max(bb.minY, Math.min(nozzlePos.y, bb.maxY));
            double cz = Math.max(bb.minZ, Math.min(nozzlePos.z, bb.maxZ));
            Vec3 toTarget = new Vec3(cx - nozzlePos.x, cy - nozzlePos.y, cz - nozzlePos.z);
            double distSq = toTarget.lengthSqr();
            if (distSq > CONE_RANGE * CONE_RANGE) continue;
            if (distSq > 0.001) {
                double dot = toTarget.normalize().dot(fireVec);
                if (dot < CONE_HALF_ANGLE_COS) continue;
            }

            target.hurt(level.damageSources().onFire(), MAGIC_DAMAGE_PER_TICK * fireMult);
            target.igniteForTicks(FIRE_TICKS);
        }

        // Spawn FIRE_ELEMENT particles (server-side send)
        if (level instanceof ServerLevel serverLevel) {
            spawnFlameParticles(serverLevel, nozzlePos, fireVec);
        }
    }

    private void spawnFlameParticles(ServerLevel level, Vec3 origin, Vec3 fireDir) {
        // Compute two orthogonal vectors for cone spread
        Vec3 perp1 = Math.abs(fireDir.y) < 0.9
                ? new Vec3(-fireDir.z, 0, fireDir.x).normalize()
                : new Vec3(1, 0, 0);
        Vec3 perp2 = fireDir.cross(perp1).normalize();

        ScaledParticleOption particle = new ScaledParticleOption(ModParticles.FIRE_ELEMENT.get(), 1.0f);
        for (int i = 0; i < 8; i++) {
            double spread1 = (level.getRandom().nextDouble() - 0.5) * Math.tan(Math.toRadians(30));
            double spread2 = (level.getRandom().nextDouble() - 0.5) * Math.tan(Math.toRadians(30));
            Vec3 dir = fireDir.add(perp1.scale(spread1)).add(perp2.scale(spread2)).normalize().scale(0.3);
            Vec3 spawnPos = origin.add(fireDir.scale(0.24f));
            level.sendParticles(particle,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    0, dir.x, dir.y, dir.z, 0.5);
        }
    }
}
