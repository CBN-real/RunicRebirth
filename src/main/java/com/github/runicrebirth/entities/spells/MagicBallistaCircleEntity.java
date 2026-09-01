package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicBallistaCircleEntity extends AbstractSpellCircleEntity {

    private static final int LIFESPAN = 84;

    private Vec3 direction;
    private float xRot;
    private float yRot;

    public MagicBallistaCircleEntity(EntityType<? extends MagicBallistaCircleEntity> type, Level level) {
        super(type, level);
    }

    public MagicBallistaCircleEntity(Level level, LivingEntity owner, SpellParams params,
                                     Vec3 direction, float xRot, float yRot, LivingEntity target) {
        super(ModEntities.MAGIC_BALLISTA_CIRCLE.get(), level);
        init(owner, params, LIFESPAN, target);
        this.direction = direction.normalize();
        this.xRot = xRot;
        this.yRot = yRot;
    }

    @Override
    protected void spawnProjectile() {
        MagicBallistaEntity ballista = new MagicBallistaEntity(level(), owner, params, direction);
        ballista.setPos(this.getX(), this.getY(), this.getZ());
        ballista.setYRot(this.getYRot());
        ballista.setXRot(this.getXRot());
        ballista.setTrackingTarget(this.target);
        level().addFreshEntity(ballista);
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
