package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicSlashCircleEntity extends AbstractSpellCircleEntity {

    private static final int LIFESPAN = 60;

    private Vec3 direction;
    private float speed;

    public MagicSlashCircleEntity(EntityType<? extends MagicSlashCircleEntity> type, Level level) {
        super(type, level);
    }

    public MagicSlashCircleEntity(Level level, LivingEntity owner, SpellParams params,
                                  Vec3 direction, float speed) {
        super(ModEntities.MAGIC_SLASH_CIRCLE.get(), level);
        init(owner, params, LIFESPAN);
        this.direction = direction.normalize();
        this.speed = speed;
    }

    @Override
    protected void spawnProjectile() {
        MagicSlashEntity slash = new MagicSlashEntity(level(), owner, params, direction);
        slash.setPos(this.getX(), this.getY(), this.getZ());

        level().addFreshEntity(slash);
    }
}
