package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicMeteorCircleEntity extends AbstractSpellCircleEntity {

    private static final int LIFESPAN = 115;

    private float directDamage;
    private float splashDamage;
    private Vec3 targetPos;

    public MagicMeteorCircleEntity(EntityType<? extends MagicMeteorCircleEntity> type, Level level) {
        super(type, level);
    }

    public MagicMeteorCircleEntity(Level level, LivingEntity owner, SpellParams params,
                                   float directDamage, float splashDamage, Vec3 targetPos, LivingEntity target) {
        super(ModEntities.MAGIC_METEOR_CIRCLE.get(), level);
        init(owner, params, LIFESPAN, target);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.targetPos = targetPos;
    }

    @Override
    protected void onSpawn() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
            ModSounds.SPELLS_METEOR_CIRCLE_INITIATE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    protected void spawnProjectile() {
        Vec3 toTarget = targetPos.subtract(this.position()).normalize();
        MagicMeteorEntity meteor = new MagicMeteorEntity(level(), owner, params, directDamage, splashDamage, toTarget);
        meteor.setPos(this.getX(), this.getY(), this.getZ());
        meteor.setTrackingTarget(this.target);
        level().addFreshEntity(meteor);
    }
}
