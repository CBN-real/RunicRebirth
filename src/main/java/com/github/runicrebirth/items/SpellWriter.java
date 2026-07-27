package com.github.runicrebirth.items;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.magic.stack.SpellResolver;
import com.github.runicrebirth.network.CastAnimBroadcastS2CPacket;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import com.github.runicrebirth.spells.types.Infusion;
import com.github.runicrebirth.util.RaycastBuilder;
import com.github.runicrebirth.util.RaycastTarget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.ArrayList;
import java.util.List;

public abstract class SpellWriter extends MagicItem {

    public SpellWriter(Properties properties) {
        super(properties);
    }

    public static WandStacksData getStacks(ItemStack stack) {
        WandStacksData data = stack.get(ModDataComponents.WAND_STACKS.get());
        if (data == null) {
            data = WandStacksData.createDefault(ServerConfig.SPELL_STACK_COUNT.get());
            stack.set(ModDataComponents.WAND_STACKS.get(), data);
        }
        return data;
    }

    public static void setStacks(ItemStack stack, WandStacksData data) {
        stack.set(ModDataComponents.WAND_STACKS.get(), data);
    }

    public static int getActiveIndex(ItemStack stack) {
        return getStacks(stack).activeIndex();
    }

    public static void cycleActiveStack(ItemStack stack) {
        WandStacksData data = getStacks(stack);
        int next = (data.activeIndex() + 1) % data.stacks().size();
        setStacks(stack, data.withActiveIndex(next));
    }

    public static SpellStack resolveActiveStack(ItemStack stack) {
        WandStacksData data = getStacks(stack);
        if (data.stacks().isEmpty()) return new SpellStack();
        WandStacksData.StackEntry entry = data.stacks().get(data.activeIndex());
        return fromEntry(entry);
    }

    public static SpellStack fromEntry(WandStacksData.StackEntry entry) {
        SpellStack ss = new SpellStack();
        for (WandStacksData.ComponentRef ref : entry.components()) {
            SpellComponent c = ref.kind() == WandStacksData.ComponentRef.KIND_TYPE
                ? SpellTypeRegistry.get(ref.id())
                : ModifierRegistry.get(ref.id());
            if (c != null) ss.forceAppend(c);
        }
        if (entry.elementId() != null) {
            var elem = ElementRegistry.get(entry.elementId());
            if (elem != null) ss.setElement(elem);
        }
        return ss;
    }

    public static WandStacksData.StackEntry toEntry(SpellStack ss, boolean inscribed, int permanentCount) {
        List<WandStacksData.ComponentRef> refs = new ArrayList<>();
        for (SpellComponent c : ss.components()) {
            int kind = c instanceof SpellType
                ? WandStacksData.ComponentRef.KIND_TYPE
                : WandStacksData.ComponentRef.KIND_MODIFIER;
            refs.add(new WandStacksData.ComponentRef(kind, c.id()));
        }
        var elem = ss.resolveElement();
        return new WandStacksData.StackEntry(
            List.copyOf(refs),
            elem != null ? elem.id() : null,
            inscribed,
            permanentCount
        );
    }

    public static void writeActiveStack(ItemStack itemStack, SpellStack ss) {
        WandStacksData data = getStacks(itemStack);
        WandStacksData.StackEntry current = data.stacks().get(data.activeIndex());
        setStacks(itemStack, data.withStack(data.activeIndex(),
            toEntry(ss, current.inscribed(), current.permanentCount())));
    }

    public static void clearActiveStack(ItemStack itemStack) {
        WandStacksData data = getStacks(itemStack);
        WandStacksData.StackEntry current = data.stacks().get(data.activeIndex());
        if (current.inscribed()) {
            setStacks(itemStack, data.withStack(data.activeIndex(), current.withClearedTemporary()));
            return;
        }
        setStacks(itemStack, data.withStack(data.activeIndex(), WandStacksData.StackEntry.EMPTY));
    }

    public static int getMaxInscriptions(ItemStack stack) {
        Integer max = stack.get(ModDataComponents.MAX_INSCRIPTIONS.get());
        return max != null ? max : 0;
    }

    public static int getMaxModifierSlots(ItemStack stack) {
        Integer max = stack.get(ModDataComponents.MAX_MODIFIER_SLOTS.get());
        return max != null ? max : 0;
    }

    public static int getInitialCharges(ItemStack stack) {
        Integer charges = stack.get(ModDataComponents.INITIAL_CHARGES.get());
        return charges != null ? charges : 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int modSlots = getMaxModifierSlots(stack);
        if (modSlots > 0) {
            tooltipComponents.add(Component.literal("+" + modSlots + " Modifier Slots").withStyle(ChatFormatting.GRAY));
        }
        Integer charges = stack.get(ModDataComponents.INITIAL_CHARGES.get());
        if (charges != null && charges > 0) {
            tooltipComponents.add(Component.literal("+" + charges + " Spell Charges").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide) return InteractionResultHolder.consume(stack);

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResultHolder.pass(stack);
        MagicData data = MagicData.of(serverPlayer);

        if (player.isShiftKeyDown()) {
            clearActiveStack(stack);
            data.clearCharges();
            StackChangedS2CPacket.sendTo(serverPlayer);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        if (data.globalCastLockoutTicks() > 0) return InteractionResultHolder.pass(stack);

        Vec3 eye = serverPlayer.getEyePosition();
        Vec3 dir = serverPlayer.getLookAngle().normalize();
        float xRot = player.getXRot();
        float yRot = player.getYRot();

        if (data.hasCharges()) {
            SpellType chargedType = SpellTypeRegistry.get(data.chargedSpellId());
            if (chargedType != null) {
                if (data.isOnCooldown(chargedType.id())) return InteractionResultHolder.pass(stack);
                SpellParams chargedParams = data.chargedParams();
                spawnCircle(serverPlayer, chargedType, chargedParams, eye, dir, xRot, yRot,
                    stack, 1 + chargedParams.extraCasts);
                int chargeCooldown = chargedParams.cooldownOverrideTicks >= 0
                    ? chargedParams.cooldownOverrideTicks : chargedType.cooldownTicks();
                data.startCooldown(chargedType.id(), chargeCooldown);
                data.consumeCharge();
                if (!data.hasCharges()) {
                    WandStacksData wandData = getStacks(stack);
                    WandStacksData.StackEntry activeEntry = wandData.stacks().get(wandData.activeIndex());
                    if (!activeEntry.inscribed()) {
                        clearActiveStack(stack);
                    } else if (!activeEntry.hasPermanentSpellType()) {
                        clearActiveStack(stack);
                    }
                }
            } else {
                data.clearCharges();
            }
            StackChangedS2CPacket.sendTo(serverPlayer);
            CastAnimBroadcastS2CPacket.broadcast(serverPlayer, 60);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        SpellStack active = resolveActiveStack(stack);
        if (active.validSpell()) {

            SpellCastContext ctx = new SpellCastContext((ServerLevel) level, serverPlayer, stack,
                eye, dir, xRot, yRot, null);
            SpellResolver.PreparedCast prepared = SpellResolver.prepare(ctx, active);
            if (prepared == null) return InteractionResultHolder.pass(stack);

            SpellType type = prepared.type();
            SpellParams params = prepared.params();

            if (data.isOnCooldown(type.id())) return InteractionResultHolder.pass(stack);

            for (SpellComponent c : active.components()) {
                if (c instanceof SpellModifier) {
                    params.modifierIds.add(c.id().getPath());
                }
            }

            int initialCharges = getInitialCharges(stack);
            boolean useChargeMode = params.useCharges || initialCharges > 1;
            int totalCharges = initialCharges * params.chargesMultiplier;
            int simultaneousCasts = 1 + params.extraCasts;

            WandStacksData wandData = getStacks(stack);
            boolean isInscribed = wandData.stacks().get(wandData.activeIndex()).inscribed();

            if (useChargeMode && totalCharges > 1) {
                spawnCircle(serverPlayer, type, params, eye, dir, xRot, yRot, stack, simultaneousCasts);
                data.setCharges(totalCharges - 1, type.id(), params);
            } else {
                spawnCircle(serverPlayer, type, params, eye, dir, xRot, yRot, stack, simultaneousCasts);
                if (isInscribed) {
                    WandStacksData.StackEntry activeEntry = wandData.stacks().get(wandData.activeIndex());
                    if (!activeEntry.hasPermanentSpellType()) {
                        clearActiveStack(stack);
                    }
                } else {
                    clearActiveStack(stack);
                }
            }

            int cooldown = params.cooldownOverrideTicks >= 0 ? params.cooldownOverrideTicks : type.cooldownTicks();
            data.startCooldown(type.id(), cooldown);
            StackChangedS2CPacket.sendTo(serverPlayer);
            CastAnimBroadcastS2CPacket.broadcast(serverPlayer, 60);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        return InteractionResultHolder.pass(stack);
    }

    private void spawnCircle(ServerPlayer player, SpellType type, SpellParams params,
                             Vec3 eye, Vec3 dir, float xRot, float yRot, ItemStack wandItem,
                             int totalCasts) {
        double range = 64.0 * params.rangeMultiplier;
        Vec3 end = eye.add(dir.normalize().scale(range));
        HitResult hit = RaycastBuilder.begin(player.level(), player)
            .start(eye).end(end)
            .checkForBlocks(true)
            .inflate(3.0f)
            .cast();

        RaycastTarget target;
        if (hit instanceof EntityHitResult ehr) {
            target = new RaycastTarget(ehr.getEntity(), null);
        } else if (hit instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.BLOCK) {
            target = new RaycastTarget(null, bhr.getLocation());
        } else {
            target = RaycastTarget.NONE;
        }

        if (totalCasts <= 1) {
            spawnSingleCircle(player, type, params, eye, dir, xRot, yRot, wandItem, 0, target, 0f);
        } else {
            for (int i = 0; i < totalCasts; i++) {
                int staggerDelay = i * type.multiCastDelay();
                float lateralOffset = computeLateralOffset(i, totalCasts) * params.size;
                spawnSingleCircle(player, type, params, eye, dir, xRot, yRot, wandItem,
                    staggerDelay, target, lateralOffset);
            }
        }
    }

    private void spawnSingleCircle(ServerPlayer player, SpellType type, SpellParams params,
                                   Vec3 eye, Vec3 dir, float xRot, float yRot, ItemStack wandItem,
                                   int extraDelayTicks, RaycastTarget target, float lateralOffset) {
        Vec3 circlePos = eye.add(dir.scale(1.0));

        if (lateralOffset != 0f) {
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = dir.cross(up).normalize();
            if (right.lengthSqr() < 1.0E-6) {
                up = new Vec3(1, 0, 0);
                right = dir.cross(up).normalize();
            }
            circlePos = circlePos.add(right.scale(lateralOffset));
        }

        Vec3 aimDir = dir;
        float aimXRot = xRot;
        float aimYRot = yRot;
        if (target.hasEntityTarget()) {
            Vec3 toTarget = target.entity().getBoundingBox().getCenter().subtract(circlePos);
            if (toTarget.lengthSqr() > 1.0E-6) {
                aimDir = toTarget.normalize();
                double dx = toTarget.x;
                double dz = toTarget.z;
                aimYRot = (float) Math.toDegrees(Math.atan2(-dx, dz));
                double hDist = Math.sqrt(dx * dx + dz * dz);
                aimXRot = (float) Math.toDegrees(Math.atan2(-toTarget.y, hDist));
            }
        } else if (target.hasBlockTarget()) {
            Vec3 toBlock = target.blockPosition().subtract(circlePos);
            if (toBlock.lengthSqr() > 1.0E-6) {
                aimDir = toBlock.normalize();
                double dx = toBlock.x;
                double dz = toBlock.z;
                aimYRot = (float) Math.toDegrees(Math.atan2(-dx, dz));
                double hDist = Math.sqrt(dx * dx + dz * dz);
                aimXRot = (float) Math.toDegrees(Math.atan2(-toBlock.y, hDist));
            }
        }

        int totalDelay = params.castingDelayTicks + extraDelayTicks;

        AbstractCircleEntity circle = type.buildCircle(
            player.level(), player, params, aimDir, wandItem,
            1, totalDelay, aimXRot, aimYRot, target);
        circle.setPos(circlePos.x, circlePos.y - 0.5, circlePos.z);
        circle.setYRot(aimYRot);
        circle.setXRot(aimXRot);

        player.level().addFreshEntity(circle);
    }

    private static float computeLateralOffset(int index, int total) {
        if (total <= 1) return 0f;
        float spacing = 1.0f;
        float halfWidth = (total - 1) * spacing / 2f;
        return -halfWidth + index * spacing;
    }
}
