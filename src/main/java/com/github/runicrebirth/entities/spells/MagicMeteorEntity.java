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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicMeteorEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 100;

    private float directDamage = 10f;
    private float splashDamage = 5f;
    private float splashRadius = 3f;

    public MagicMeteorEntity(EntityType<? extends MagicMeteorEntity> type, Level level) {
        super(type, level);
    }

    public MagicMeteorEntity(Level level, LivingEntity owner, SpellParams params,
                             float directDamage, float splashDamage, Vec3 direction) {
        super(ModEntities.MAGIC_METEOR.get(), owner, level, direction, params.speed);
        initFromParams(params);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.splashRadius = 3f * params.size;
    }

    @Override
    protected double getDefaultGravity() {
        return getPhase() == SpellPhase.ACTIVE ? 0.08 : 0.0;
    }

    @Override
    protected void onActiveTick() {
        if (age > MAX_LIFETIME_TICKS) {
            beginEnding();
        }
    }

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y + 0.3, pos.z, 0.0, 0.1, 0.0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
            DamageSources.applyDamage(target, directDamage, source);
        } else {
            target.hurt(this.damageSources().magic(), directDamage);
        }
        explodeSplash(result.getLocation());
        beginEnding();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        explodeSplash(Vec3.atCenterOf(result.getBlockPos()));
        beginEnding();
    }

    private void explodeSplash(Vec3 center) {
        if (!(this.level() instanceof ServerLevel server)) return;
        Entity owner = this.getOwner();
        for (LivingEntity target : Utils.entitiesInRange(server, center, splashRadius, this)) {
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
                DamageSources.applyDamage(target, splashDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), splashDamage);
            }
        }
        ParticleHelper.burstParticleEvent(server, element().particle(), center,
            (int) (40 * size), 0.5 * size, 0.5 * size, 0.5 * size, 0.1, 1.0f);

        ImpactHelper.createImpact(server, center, splashRadius, element(), 1.5f);
    }
}
