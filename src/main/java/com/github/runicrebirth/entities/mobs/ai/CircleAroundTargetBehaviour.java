package com.github.runicrebirth.entities.mobs.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;

import java.util.Set;

public class CircleAroundTargetBehaviour<T extends PathfinderMob> extends ExtendedBehaviour<T> {

    private final double orbitRadius;
    private boolean circleSide = false;
    private int sideToggleTimer = 0;

    public CircleAroundTargetBehaviour(double orbitRadius) {
        this.orbitRadius = orbitRadius;
    }

    @Override
    public Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return Set.of(new MemoryCondition.Present<>(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected boolean shouldKeepRunning(T entity) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void tick(T entity) {
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> {
            entity.getLookControl().setLookAt(target, 30.0f, 30.0f);
            sideToggleTimer++;
            if (sideToggleTimer >= 60) {
                circleSide = !circleSide;
                sideToggleTimer = 0;
            }
            Vec3 toMob = entity.position().subtract(target.position());
            double dist = toMob.horizontalDistance();
            if (dist < 0.01) return;

            Vec3 right = new Vec3(-toMob.z / dist, 0, toMob.x / dist);
            if (!circleSide) right = right.scale(-1);

            Vec3 strafeTarget = target.position()
                .add(toMob.normalize().scale(orbitRadius))
                .add(right.scale(2.0));
            entity.getNavigation().moveTo(strafeTarget.x, strafeTarget.y, strafeTarget.z, 1.0);
        });
    }
}
