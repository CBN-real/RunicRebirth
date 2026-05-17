package com.github.interactivemagic.util;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record RaycastTarget(@Nullable Entity entity, @Nullable Vec3 blockPosition) {

    public static final RaycastTarget NONE = new RaycastTarget(null, null);

    public boolean hasEntityTarget() {
        return entity != null;
    }

    public boolean hasBlockTarget() {
        return blockPosition != null;
    }

    public boolean isEmpty() {
        return entity == null && blockPosition == null;
    }
}
