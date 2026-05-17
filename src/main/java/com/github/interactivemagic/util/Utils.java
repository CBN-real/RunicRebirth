package com.github.interactivemagic.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Utils {

    private Utils() {}

    public static boolean canHitWithRaycast(Entity entity) {
        return entity != null && !entity.isSpectator() && entity.isPickable() && entity.isAlive();
    }

    public static double softCapFormula(double value) {
        if (value < 1.0) return value;
        return 1.0 + Math.log(value);
    }

    public static BlockHitResult raycastBlocks(Level level, Vec3 start, Vec3 end, Entity source) {
        return level.clip(new ClipContext(start, end,
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
    }

    public static EntityHitResult raycastEntities(Level level, Entity origin, Vec3 start, Vec3 end,
                                                   AABB searchBox, Predicate<? super Entity> filter, double maxDistSqr) {
        Entity best = null;
        Vec3 bestPos = null;
        double bestDistSqr = maxDistSqr;
        for (Entity e : level.getEntities(origin, searchBox, filter)) {
            AABB aabb = e.getBoundingBox().inflate(0.3);
            java.util.Optional<Vec3> hit = aabb.clip(start, end);
            if (hit.isPresent()) {
                double d = start.distanceToSqr(hit.get());
                if (d < bestDistSqr) {
                    best = e;
                    bestPos = hit.get();
                    bestDistSqr = d;
                }
            }
        }
        return best == null ? null : new EntityHitResult(best, bestPos);
    }

    public static List<LivingEntity> entitiesInRange(Level level, Vec3 center, double radius, Entity ignore) {
        AABB bb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        List<LivingEntity> out = new ArrayList<>();
        for (Entity e : level.getEntities(ignore, bb)) {
            if (e instanceof LivingEntity le && e.distanceToSqr(center) <= radius * radius) {
                out.add(le);
            }
        }
        return out;
    }

    public static List<LivingEntity> entitiesInCone(Level level, Vec3 apex, Vec3 direction, double range,
                                                     double halfAngleDeg, Entity ignore) {
        Vec3 dir = direction.normalize();
        double cosHalf = Math.cos(Math.toRadians(halfAngleDeg));
        List<LivingEntity> out = new ArrayList<>();
        AABB bb = new AABB(apex.subtract(range, range, range), apex.add(range, range, range));
        for (Entity e : level.getEntities(ignore, bb)) {
            if (!(e instanceof LivingEntity le)) continue;
            Vec3 toEntity = e.getBoundingBox().getCenter().subtract(apex);
            double d = toEntity.length();
            if (d > range || d < 1.0E-4) continue;
            double dot = toEntity.scale(1.0 / d).dot(dir);
            if (dot >= cosHalf) out.add(le);
        }
        return out;
    }

    public static Vec3 lookVector(Player player) {
        return player.getLookAngle().normalize();
    }

    public static float lerpAngle(float a, float b, float t) {
        return a + Mth.wrapDegrees(b - a) * t;
    }

    public static String timeFromTicks(int ticks, int decimals) {
        double s = ticks / 20.0;
        return String.format("%." + decimals + "f", s);
    }

    public static double randScaled(double scale) {
      return (2.0D * Math.random() - 1.0D) * scale;
    }

    public static Vec3 randVec3(double scale) {
      return new Vec3(
          randScaled(scale),
          randScaled(scale),
          randScaled(scale)
      );
    }
}
