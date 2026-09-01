package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.MagicHandEntity;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.network.MagicHandSyncS2CPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagicHandRingItem extends MagicItem implements IActivatableRing {

    private static final double RANGE = 8.0;
    private static final double AIM_COS = 0.9;

    public MagicHandRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;
        MagicData data = MagicData.of(player);

        if (data.isOnCooldown(MagicHandEntity.COOLDOWN_KEY)) return;

        int existingId = data.magicHandEntityId();
        if (existingId != -1) {
            Entity existing = level.getEntity(existingId);
            if (existing instanceof MagicHandEntity hand) {
                hand.beginEnding();
                return;
            }
            data.clearMagicHandEntityId();
        }

        LivingEntity target = findTarget(player, level);
        if (target == null) return;

        boolean isPassive = target.getType().getCategory() != MobCategory.MONSTER;
        MagicHandEntity hand = MagicHandEntity.create(player, target, isPassive);
        level.addFreshEntity(hand);
        data.setMagicHandEntityId(hand.getId());

        int syncTicks = isPassive ? -1 : MagicHandEntity.HOSTILE_HOLD_TICKS;
        MagicHandSyncS2CPacket.sendTo(player, syncTicks, isPassive);
    }

    private LivingEntity findTarget(ServerPlayer player, ServerLevel level) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();

        AABB searchBox = player.getBoundingBox().expandTowards(lookDir.scale(RANGE)).inflate(1.0);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox,
            e -> e != player && e.isAlive());

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;

        for (LivingEntity candidate : candidates) {
            Vec3 toEntity = candidate.getBoundingBox().getCenter().subtract(eyePos);
            double dist = toEntity.length();
            if (dist > RANGE) continue;
            if (toEntity.normalize().dot(lookDir) < AIM_COS) continue;
            if (dist < bestDist) {
                bestDist = dist;
                best = candidate;
            }
        }
        return best;
    }
}
