package com.github.interactivemagic.items;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.registry.SpellTypeRegistry;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellComponent;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.capabilities.magic.MagicData;
import com.github.interactivemagic.entities.spells.BasicCircleEntity;
import com.github.interactivemagic.magic.stack.SpellResolver;
import com.github.interactivemagic.network.StackChangedS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SpellWriter extends MagicItem {

    public SpellWriter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(stack, true);

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.pass(stack);
        MagicData data = MagicData.of(serverPlayer);

        if (player.isShiftKeyDown()) {
            SpellStack active = data.activeStack();
            if (active != null) active.clear();
            data.clearCharges();
            StackChangedS2CPacket.sendTo(serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        if (data.isCastingInProgress()) return InteractionResultHolder.pass(stack);
        if (data.globalCastLockoutTicks() > 0) return InteractionResultHolder.pass(stack);

        if (data.hasCharges()) {
            SpellType chargedType = SpellTypeRegistry.get(data.chargedSpellId());
            if (chargedType != null) {
                spawnCircle(serverPlayer, data, chargedType, data.chargedParams(), stack, 1);
                data.consumeCharge();
                if (!data.hasCharges()) {
                    SpellStack active = data.activeStack();
                    if (active != null) active.clear();
                }
            } else {
                data.clearCharges();
            }
            StackChangedS2CPacket.sendTo(serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        SpellStack active = data.activeStack();
        if (active != null && active.validSpell()) {
            Vec3 eye = serverPlayer.getEyePosition();
            Vec3 dir = serverPlayer.getLookAngle().normalize();
            SpellCastContext ctx = new SpellCastContext((ServerLevel) level, serverPlayer, stack, eye, dir);
            SpellResolver.PreparedCast prepared = SpellResolver.prepare(ctx, active);
            if (prepared == null) return InteractionResultHolder.pass(stack);

            SpellType type = prepared.type();
            SpellParams params = prepared.params();

            for (SpellComponent c : active.components()) {
                if (c instanceof SpellModifier) {
                    params.modifierIds.add(c.id().getPath());
                }
            }

            int totalCasts = 1 + params.extraCasts;

            if (params.useCharges && totalCasts > 1) {
                spawnCircle(serverPlayer, data, type, params, stack, 1);
                data.setCharges(totalCasts - 1, type.id(), params);
            } else {
                spawnCircle(serverPlayer, data, type, params, stack, totalCasts);
                active.clear();
            }

            int cooldown = params.cooldownOverrideTicks >= 0 ? params.cooldownOverrideTicks : type.cooldownTicks();
            data.startCooldown(type.id(), cooldown);
            StackChangedS2CPacket.sendTo(serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        return InteractionResultHolder.pass(stack);
    }

    private void spawnCircle(ServerPlayer player, MagicData data,
                             SpellType type, SpellParams params,
                             ItemStack wandItem, int totalCasts) {
        Vec3 eye = player.getEyePosition();
        Vec3 dir = player.getLookAngle().normalize();
        Vec3 circlePos = eye.add(dir.scale(1.0));

        BasicCircleEntity circle = new BasicCircleEntity(
            player.level(), player, type, params, dir, wandItem,
            totalCasts, params.castingDelayTicks);
        circle.setPos(circlePos.x, circlePos.y, circlePos.z);
        circle.setYRot(player.getYRot());
        circle.setXRot(player.getXRot());


        data.setCastingInProgress(true);
    }
}
