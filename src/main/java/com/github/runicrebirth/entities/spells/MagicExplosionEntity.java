package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.util.ImpactHelper;
import com.github.runicrebirth.util.ParticleHelper;
import com.github.runicrebirth.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicExplosionEntity extends AbstractInstantSpellEntity {

    private float aoeRadius = 1.5f;
    private UUID casterUUID;

    public MagicExplosionEntity(EntityType<? extends MagicExplosionEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 15;
        this.endTicks = 10;
    }

    public MagicExplosionEntity(Level level, LivingEntity caster, SpellParams params) {
        this(ModEntities.MAGIC_EXPLOSION.get(), level);
        this.casterUUID = caster.getUUID();
        initFromParams(params);
        this.aoeRadius = 1.5f * params.size;
    }

    @Override
    protected void onActivated() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 center = this.position();
        Entity caster = server.getEntity(casterUUID);

        for (LivingEntity target : Utils.entitiesInRange(server, center, aoeRadius, this)) {
            if (caster instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                    .withSpellType(com.github.runicrebirth.spells.types.MagicExplosion.ID);
                DamageSources.applyDamage(target, damage, source);
            } else {
                target.hurt(this.damageSources().magic(), damage);
            }
        }

        ParticleHelper.burstParticleEvent(server, element().particle(), center,
            (int) (40 * size), 0.5 * size, 0.5 * size, 0.5 * size, 0.1, 1.0f);

        ImpactHelper.createImpact(server, center, aoeRadius, element(), 1.5f);

        beginEnding();
    }

    @Override
    protected void onActiveTick() {}

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y + 0.3, pos.z, 0.0, 0.05, 0.0);
    }

}
