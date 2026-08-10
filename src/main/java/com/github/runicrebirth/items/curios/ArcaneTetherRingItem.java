package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.spells.ArcaneTetherEntity;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.MagicItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

import java.util.List;

public class ArcaneTetherRingItem extends MagicItem implements IActivatableRing {

    private static final double TETHER_RANGE = 24.0;

    public ArcaneTetherRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (MagicData.of(player).isOnCooldown(ArcaneTetherEntity.COOLDOWN_KEY)) return;

        // Toggle off existing tether
        AABB searchBox = player.getBoundingBox().inflate(TETHER_RANGE + 4);
        List<ArcaneTetherEntity> existing = level.getEntitiesOfClass(
            ArcaneTetherEntity.class, searchBox,
            e -> player.getUUID().equals(e.getOwnerUUID()));
        if (!existing.isEmpty()) {
            existing.forEach(net.minecraft.world.entity.Entity::discard);
            return;
        }

        // Raycast 24 blocks
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();
        Vec3 rayEnd = eyePos.add(lookDir.scale(TETHER_RANGE));

        // Block hit
        BlockHitResult blockHit = level.clip(new ClipContext(
            eyePos, rayEnd,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            player));

        // Entity hit
        AABB entitySearch = new AABB(eyePos, rayEnd).inflate(1.0);
        EntityHitResult entityHit = null;
        double closestDistSqr = TETHER_RANGE * TETHER_RANGE;
        for (net.minecraft.world.entity.Entity candidate : level.getEntities(player, entitySearch)) {
            if (!candidate.isPickable()) continue;
            AABB hitBox = candidate.getBoundingBox().inflate(0.2);
            var result = hitBox.clip(eyePos, rayEnd);
            if (result.isPresent()) {
                double distSqr = eyePos.distanceToSqr(result.get());
                if (distSqr < closestDistSqr) {
                    closestDistSqr = distSqr;
                    entityHit = new EntityHitResult(candidate, result.get());
                }
            }
        }

        // Prefer entity hit if closer than block hit
        Vec3 anchorPos;
        int attachedEntityId = -1;

        if (entityHit != null && (blockHit.getType() == HitResult.Type.MISS
                || eyePos.distanceToSqr(entityHit.getLocation()) < eyePos.distanceToSqr(blockHit.getLocation()))) {
            Vec3 center = entityHit.getEntity().getBoundingBox().getCenter();
            anchorPos = center;
            attachedEntityId = entityHit.getEntity().getId();
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            anchorPos = blockHit.getLocation();
        } else {
            return; // No hit within range
        }

        ArcaneTetherEntity tether = new ArcaneTetherEntity(
            level,
            player.getUUID(),
            (float) anchorPos.x,
            (float) anchorPos.y,
            (float) anchorPos.z,
            attachedEntityId);
        tether.setPos(anchorPos.x, anchorPos.y, anchorPos.z);
        level.addFreshEntity(tether);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            ModSounds.SPELLS_TETHER.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
