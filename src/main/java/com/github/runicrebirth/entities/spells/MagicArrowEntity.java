package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicArrowEntity extends AbstractProjectileSpellEntity {

    private static final int MAX_LIFETIME_TICKS = 200;

    public MagicArrowEntity(EntityType<? extends MagicArrowEntity> type, Level level) {
        super(type, level);
        this.damageCategory = MagicDamageType.SHARP;
        this.endTicks = 15;
        this.chargeTicks = 18;
    }

    public MagicArrowEntity(Level level, LivingEntity owner, SpellParams params, Vec3 direction) {
        super(ModEntities.MAGIC_ARROW.get(), owner, level, direction, params.speed);
        initFromParams(params);

    }


    @Override
    protected void onActiveTick() {
        if (age > MAX_LIFETIME_TICKS) {
            beginEnding();
        }
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                .withSpellType(com.github.runicrebirth.spells.types.MagicArrow.ID);
            DamageSources.applyDamage(target, damage, source);
        } else {
            target.hurt(this.damageSources().magic(), damage);
        }
        burstParticles();
        beginEnding();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        burstParticles();
        beginEnding();
    }
}
