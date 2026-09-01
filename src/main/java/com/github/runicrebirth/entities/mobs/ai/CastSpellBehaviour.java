package com.github.runicrebirth.entities.mobs.ai;

import com.github.runicrebirth.api.spells.SpellCastContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;
import net.tslat.smartbrainlib.library.object.MemoryTest;

import java.util.Set;

public abstract class CastSpellBehaviour<T extends Monster> extends ExtendedBehaviour<T> {

    private final int cooldownTicks;
    private final int windupTicks;
    protected int cooldownTimer = 0;
    private int windupTimer = 0;
    private LivingEntity pendingTarget = null;

    protected CastSpellBehaviour(int cooldownTicks) {
        this(cooldownTicks, 0);
    }

    protected CastSpellBehaviour(int cooldownTicks, int windupTicks) {
        this.cooldownTicks = cooldownTicks;
        this.windupTicks = windupTicks;
    }

    @Override
    public Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T entity) {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected boolean shouldKeepRunning(T entity) {
        return windupTimer > 0;
    }

    @Override
    protected void start(T entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> {
            entity.getLookControl().setLookAt(target, 30.0f, 30.0f);
            if (windupTicks > 0) {
                onWindupStart(serverLevel, entity, target);
                pendingTarget = target;
                windupTimer = windupTicks;
            } else {
                SpellCastContext ctx = buildContext(serverLevel, entity, target);
                performCast(serverLevel, entity, target, ctx);
                cooldownTimer = cooldownTicks;
            }
        });
    }

    @Override
    protected void tick(ServerLevel level, T entity, long gameTime) {
        if (windupTimer > 0) {
            windupTimer--;
            if (pendingTarget != null) entity.getLookControl().setLookAt(pendingTarget, 30.0f, 30.0f);
            if (windupTimer == 0 && pendingTarget != null) {
                SpellCastContext ctx = buildContext(level, entity, pendingTarget);
                performCast(level, entity, pendingTarget, ctx);
                pendingTarget = null;
                cooldownTimer = cooldownTicks;
            }
        }
    }

    protected void onWindupStart(ServerLevel level, T entity, LivingEntity target) {}

    protected SpellCastContext buildContext(ServerLevel level, T entity, LivingEntity target) {
        Vec3 aimStart = entity.getEyePosition();
        Vec3 aimDir = entity.getLookAngle();
        return new SpellCastContext(level, entity, ItemStack.EMPTY, aimStart, aimDir,
            entity.getXRot(), entity.getYRot(), target);
    }

    protected abstract void performCast(ServerLevel level, T entity, LivingEntity target, SpellCastContext ctx);
}
