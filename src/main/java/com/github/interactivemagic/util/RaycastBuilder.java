package com.github.interactivemagic.util;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class RaycastBuilder {

    private final Level level;
    private final Entity originEntity;
    private Vec3 start;
    private Vec3 end;
    private boolean checkForBlocks = false;
    private float bbInflation = 0f;
    private Predicate<? super Entity> filter = Utils::canHitWithRaycast;

    public RaycastBuilder(Level level, Entity originEntity) {
        this.level = level;
        this.originEntity = originEntity;
    }

    public static RaycastBuilder begin(Level level, Entity originEntity) {
        return new RaycastBuilder(level, originEntity);
    }

    public RaycastBuilder start(Vec3 start) { this.start = start; return this; }
    public RaycastBuilder end(Vec3 end) { this.end = end; return this; }
    public RaycastBuilder checkForBlocks(boolean v) { this.checkForBlocks = v; return this; }
    public RaycastBuilder inflate(float inflation) { this.bbInflation = inflation; return this; }
    public RaycastBuilder filter(Predicate<? super Entity> filter) { this.filter = filter; return this; }

    public HitResult cast() {
        Vec3 effectiveEnd = this.end;
        if (checkForBlocks) {
            BlockHitResult block = Utils.raycastBlocks(level, start, end, originEntity);
            if (block.getType() != HitResult.Type.MISS) {
                effectiveEnd = block.getLocation();
            }
        }
        AABB searchBox = new AABB(start, effectiveEnd).inflate(bbInflation);
        double maxDistSqr = start.distanceToSqr(effectiveEnd);
        EntityHitResult ehr = Utils.raycastEntities(level, originEntity, start, effectiveEnd, searchBox, filter, maxDistSqr);
        if (ehr != null) return ehr;
        if (checkForBlocks) return Utils.raycastBlocks(level, start, end, originEntity);
        return BlockHitResult.miss(effectiveEnd, net.minecraft.core.Direction.UP, net.minecraft.core.BlockPos.containing(effectiveEnd));
    }
}
