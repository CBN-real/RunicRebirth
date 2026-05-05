package com.github.interactivemagic.magic.stack;

import com.github.interactivemagic.api.events.SpellPostCastEvent;
import com.github.interactivemagic.api.events.SpellPreCastEvent;
import com.github.interactivemagic.api.item.ISpellEmpowerment;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.capabilities.magic.MagicData;
import com.github.interactivemagic.config.ServerConfig;
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

        MagicData data = MagicData.of(ctx.caster());
        if (data.globalCastLockoutTicks() > 0) return null;

        SpellType type = stack.resolveType();
        if (data.isOnCooldown(type.id())) return null;

        SpellParams params = new SpellParams(
            type.baseDamage(), type.baseSize(), type.baseSpeed(),
            type.baseDuration(), 0, type.defaultElement(), type.damageCategory());

        stack.compose(params);
        applyCurioEmpowerments(ctx, params);

        SpellPreCastEvent preEvent = new SpellPreCastEvent(ctx, type, params);
        if (NeoForge.EVENT_BUS.post(preEvent).isCanceled()) return null;

        return new PreparedCast(type, params);
    }

    public static CastResult executeCast(SpellCastContext ctx, SpellType type, SpellParams params) {
        CastResult result = type.onCast(ctx, params);

        if (result == CastResult.SUCCESS) {
            MagicData data = MagicData.of(ctx.caster());
            int cooldown = params.cooldownOverrideTicks >= 0 ? params.cooldownOverrideTicks : type.cooldownTicks();
            data.startCooldown(type.id(), cooldown);
            data.setGlobalCastLockout(ServerConfig.GLOBAL_CAST_LOCKOUT_TICKS.get());
            NeoForge.EVENT_BUS.post(new SpellPostCastEvent(ctx, type, params));
        }
        return result;
    }

    public static CastResult cast(SpellCastContext ctx, SpellStack stack) {
        PreparedCast prepared = prepare(ctx, stack);
        if (prepared == null) return CastResult.FAILED;
        return executeCast(ctx, prepared.type(), prepared.params());
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
