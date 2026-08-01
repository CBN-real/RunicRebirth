package com.github.runicrebirth.magic.stack;

import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.api.events.SpellPostCastEvent;
import com.github.runicrebirth.api.events.SpellPreCastEvent;
import com.github.runicrebirth.api.item.ISpellEmpowerment;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public final class SpellResolver {

    private SpellResolver() {}

    public record PreparedCast(SpellType type, SpellParams params) {}

    public static PreparedCast prepare(SpellCastContext ctx, SpellStack stack) {
        if (stack == null || !stack.validSpell()) return null;
        if (!(ctx.caster() instanceof ServerPlayer player)) return null;

        MagicData data = MagicData.of(player);
        if (data.globalCastLockoutTicks() > 0) return null;

        SpellType type = stack.resolveType();
        if (!SpellAdvancementHelper.hasSpellUnlocked(player, type)) return null;
        if (data.isOnCooldown(type.id())) return null;

        Element resolvedElement = stack.resolveElement() != null ? stack.resolveElement() : type.defaultElement();
        SpellParams params = new SpellParams(
            type.baseDamage(), type.baseSize(), type.baseSpeed(),
            type.baseDuration(), type.castingDelayTicks(),0, resolvedElement, type.damageCategory());

        stack.compose(params);
        applyCurioEmpowerments(ctx, params);
        applyArmorEmpowerments(ctx, params);

        params.damage = params.damage * (1.0f + params.size) / 2.0f;

        SpellPreCastEvent preEvent = new SpellPreCastEvent(ctx, type, params);
        if (NeoForge.EVENT_BUS.post(preEvent).isCanceled()) return null;

        return new PreparedCast(type, params);
    }

    public static CastResult executeCast(SpellCastContext ctx, SpellType type, SpellParams params) {
        CastResult result = type.onCast(ctx, params);

        if (result == CastResult.SUCCESS) {
            if (ctx.caster() instanceof ServerPlayer player) {
                MagicData data = MagicData.of(player);
                int cooldown = params.cooldownOverrideTicks >= 0 ? params.cooldownOverrideTicks : type.cooldownTicks();
                data.startCooldown(type.id(), cooldown);
                data.setGlobalCastLockout(ServerConfig.GLOBAL_CAST_LOCKOUT_TICKS.get());
            }
            NeoForge.EVENT_BUS.post(new SpellPostCastEvent(ctx, type, params));
        }
        return result;
    }

    public static SpellParams buildParams(SpellCastContext ctx, SpellStack stack) {
        if (stack == null || !stack.validSpell()) return null;
        SpellType type = stack.resolveType();
        if (type == null) return null;
        Element resolvedElement = stack.resolveElement() != null ? stack.resolveElement() : type.defaultElement();
        SpellParams params = new SpellParams(
            type.baseDamage(), type.baseSize(), type.baseSpeed(),
            type.baseDuration(), type.castingDelayTicks(), 0, resolvedElement, type.damageCategory());
        stack.compose(params);
        applyCurioEmpowerments(ctx, params);
        applyArmorEmpowerments(ctx, params);
        params.damage = params.damage * (1.0f + params.size) / 2.0f;
        return params;
    }

    public static CastResult cast(SpellCastContext ctx, SpellStack stack) {
        PreparedCast prepared = prepare(ctx, stack);
        if (prepared == null) return CastResult.FAILED;
        return executeCast(ctx, prepared.type(), prepared.params());
    }

    private static void applyArmorEmpowerments(SpellCastContext ctx, SpellParams params) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armorStack = ctx.caster().getItemBySlot(slot);
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ISpellEmpowerment emp) {
                emp.contribute(armorStack, ctx).forEach(m -> m.apply(params));
            }
        }
    }

    private static void applyCurioEmpowerments(SpellCastContext ctx, SpellParams params) {
        Optional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(ctx.caster());
        if (handler.isEmpty()) return;
        ICuriosItemHandler inv = handler.get();
        inv.getCurios().values().forEach(stacksHandler -> {
            var stacks = stacksHandler.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack accessoryStack = stacks.getStackInSlot(i);
                if (!accessoryStack.isEmpty() && accessoryStack.getItem() instanceof ISpellEmpowerment emp) {
                    emp.contribute(accessoryStack, ctx).forEach(m -> m.apply(params));
                }
            }
        });
    }
}
