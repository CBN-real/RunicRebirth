package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class MagicSlashEntity extends AbstractProjectileSpellEntity {

    private static final double MAX_TRAVEL_DISTANCE = 16.0;

    private final IntSet hitEntities = new IntOpenHashSet();
    private Vec3 spawnPos;

    public MagicSlashEntity(EntityType<? extends MagicSlashEntity> type, Level level) {
        super(type, level);
        this.damageCategory = MagicDamageType.SHARP;
    }

    public MagicSlashEntity(Level level, LivingEntity owner, SpellParams params) {
        super(ModEntities.MAGIC_SLASH.get(), owner, level);
        initFromParams(params);
    }

    @Override
    protected void onActivated() {
        spawnPos = this.position();
    }

    @Override
    protected void onActiveTick() {
        if (spawnPos == null) spawnPos = this.position();
        double traveled = this.position().distanceTo(spawnPos);
        if (traveled >= MAX_TRAVEL_DISTANCE * size) {
            burstParticles();
            beginEnding();
        }
    }

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        Entity target = result.getEntity();
        if (hitEntities.contains(target.getId())) return;
        hitEntities.add(target.getId());
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
            DamageSources.applyDamage(target, damage, source);
        } else {
            target.hurt(this.damageSources().magic(), damage);
        }
        if (this.level() instanceof ServerLevel server) {
            Vec3 pos = result.getLocation();
            server.sendParticles(element().particle(), pos.x, pos.y, pos.z, 8, 0.2, 0.2, 0.2, 0.02);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || getPhase() != SpellPhase.ACTIVE) return;
        burstParticles();
        beginEnding();
    }

    @Override
    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        int count = (int) (18 * size);
        server.sendParticles(element().particle(), pos.x, pos.y, pos.z, count,
            0.3 * size, 0.1 * size, 0.3 * size, 0.05);
    }
}
