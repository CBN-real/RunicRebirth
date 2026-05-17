package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicHammerEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 160;

    private float directDamage = 10f;
    private float splashDamage = 5f;
    private float directRadius = 1.5f;
    private float splashRadius = 2.5f;

    public MagicHammerEntity(EntityType<? extends MagicHammerEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 60;
    }

    public MagicHammerEntity(Level level, LivingEntity owner, SpellParams params,
                             float directDamage, float splashDamage) {
        super(ModEntities.MAGIC_HAMMER.get(), owner, level);
        this.chargeTicks = 60;
        initFromParams(params);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.directRadius = 1.5f * params.size;
        this.splashRadius = 2.5f * params.size;
    }

    @Override
    protected double getDefaultGravity() {
        return getPhase() == SpellPhase.ACTIVE ? 0.08 : 0.0;
    }

    @Override
    protected void onActivated() {
        this.setDeltaMovement(0, -0.5, 0);
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
        this.level().addParticle(element().particle(), pos.x, pos.y + 0.3, pos.z, 0.0, 0.05, 0.0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        impact(result.getLocation());
        beginEnding();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        impact(Vec3.atCenterOf(result.getBlockPos()));
        beginEnding();
    }

    private void impact(Vec3 center) {
        if (!(this.level() instanceof ServerLevel server)) return;
        Entity owner = this.getOwner();

        for (LivingEntity target : Utils.entitiesInRange(server, center, directRadius, this)) {
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
                DamageSources.applyDamage(target, directDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), directDamage);
            }
        }

        for (LivingEntity target : Utils.entitiesInRange(server, center, splashRadius, this)) {
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
                DamageSources.applyDamage(target, splashDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), splashDamage);
            }
        }

        int count = (int) (50 * size);
        server.sendParticles(element().particle(), center.x, center.y, center.z, count,
            0.6 * size, 0.6 * size, 0.6 * size, 0.12);
    }
}
