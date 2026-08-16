package com.github.runicrebirth.entities.mobs.ai;

import com.github.runicrebirth.api.spells.SpellCastContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;

public abstract class CastSpellBehaviour<T extends Monster> extends ExtendedBehaviour<T> {

    private final int cooldownTicks;
    protected int cooldownTimer = 0;

    protected CastSpellBehaviour(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return List.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
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
    protected void start(T entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> {
            entity.getLookControl().setLookAt(target, 30.0f, 30.0f);
            SpellCastContext ctx = buildContext(serverLevel, entity, target);
            performCast(serverLevel, entity, target, ctx);
            cooldownTimer = cooldownTicks;
        });
    }

    protected SpellCastContext buildContext(ServerLevel level, T entity, LivingEntity target) {
        Vec3 aimStart = entity.getEyePosition();
        Vec3 aimDir = entity.getLookAngle();
        return new SpellCastContext(level, entity, ItemStack.EMPTY, aimStart, aimDir,
            entity.getXRot(), entity.getYRot(), target);
    }

    protected abstract void performCast(ServerLevel level, T entity, LivingEntity target, SpellCastContext ctx);
}
