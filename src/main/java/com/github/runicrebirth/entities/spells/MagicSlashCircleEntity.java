package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicSlashCircleEntity extends AbstractSpellCircleEntity {

    private static final int LIFESPAN = 82;

    private Vec3 direction;
    private float speed;

    public MagicSlashCircleEntity(EntityType<? extends MagicSlashCircleEntity> type, Level level) {
        super(type, level);
    }

    public MagicSlashCircleEntity(Level level, LivingEntity owner, SpellParams params,
                                  Vec3 direction, float speed, LivingEntity target) {
        super(ModEntities.MAGIC_SLASH_CIRCLE.get(), level);
        init(owner, params, LIFESPAN, target);
        this.direction = direction.normalize();
        this.speed = speed;
    }

    @Override
    protected void spawnProjectile() {
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
            ModSounds.SPELLS_SLASH_SPELL.get(), SoundSource.PLAYERS, 0.6f, 1.0f);
        MagicSlashEntity slash = new MagicSlashEntity(level(), owner, params, direction);
        Vec3 spawnPos = getCircleSpawnPos(0.5f);
        slash.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        slash.setTrackingTarget(this.target);
        level().addFreshEntity(slash);
    }
}
