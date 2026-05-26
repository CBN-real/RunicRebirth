package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.util.RaycastTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BasicCircleEntity extends AbstractCircleEntity {

    public BasicCircleEntity(EntityType<? extends BasicCircleEntity> type, Level level) {
        super(type, level);
    }

    public BasicCircleEntity(Level level, ServerPlayer caster, SpellType spellType,
                             SpellParams params, Vec3 aimDirection, ItemStack wandItem,
                             int totalCasts, int castingDelayTicks, int lifespan,
                             float xRot, float yRot, RaycastTarget target) {
        super(ModEntities.BASIC_CIRCLE.get(), level, caster, spellType, params,
            aimDirection, wandItem, totalCasts, castingDelayTicks, lifespan, xRot, yRot, target);
    }

  @Override
  protected float getCircleHeight() {
    return 0.5f;
  }
}
