package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicBlastEntity extends AbstractInstantSpellEntity {

    private static final int ACTIVE_DURATION_TICKS = 26;

    private UUID ownerUUID;
    private Vec3 aimDirection = Vec3.ZERO;
    private boolean hasFired;

    public MagicBlastEntity(EntityType<? extends MagicBlastEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 0;
        this.endTicks = 20;
    }

    public void init(LivingEntity owner, Vec3 spawnPos, Vec3 dir, SpellParams params) {
        this.ownerUUID = owner.getUUID();
        this.aimDirection = dir;
        initFromParams(params);
        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        float yaw = (float) Math.toDegrees(Math.atan2(-dir.x, dir.z));
        float pitch = (float) Math.toDegrees(Math.atan2(-dir.y, dir.horizontalDistance()));
        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    @Override
    protected void onActiveTick() {
        if (!(this.level() instanceof ServerLevel server)) return;

        if (age > ACTIVE_DURATION_TICKS) {
            beginEnding();
            return;
        }

        if (hasFired || age < 8) return;
        hasFired = true;
        level().playSound(null, blockPosition(), ModSounds.SPELLS_SHIELD_HIT.get(),
                SoundSource.PLAYERS, 1.0f, 1.0f);

        if (ownerUUID == null) {
            beginEnding();
            return;
        }
        LivingEntity owner = null;
        if (server.getEntity(ownerUUID) instanceof LivingEntity living) {
            owner = living;
        }

        Vec3 pos = this.position();
        Vec3 forward = aimDirection.normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-6) {
            right = forward.cross(new Vec3(1, 0, 0));
        }
        right = right.normalize();
        Vec3 up = right.cross(forward).normalize();

        double halfWidth = 2 * size;
        double halfHeight = 2 * size;
        double length = 3.0 * size;

        double upRad = Math.toRadians(35.0);
        double horizLen = Math.sqrt(forward.x * forward.x + forward.z * forward.z);
        Vec3 knockbackDir = new Vec3(forward.x, horizLen * Math.tan(upRad), forward.z).normalize();

        double searchRadius = length + halfWidth;
        AABB bb = new AABB(
            pos.subtract(searchRadius, searchRadius, searchRadius),
            pos.add(searchRadius, searchRadius, searchRadius));

        for (Entity e : server.getEntities(owner, bb)) {
            if (!(e instanceof LivingEntity target)) continue;
            Vec3 offset = e.getBoundingBox().getCenter().subtract(pos);
            double fwd = offset.dot(forward);
            double rgt = offset.dot(right);
            double upd = offset.dot(up);
            if (fwd < 0 || fwd > length || Math.abs(rgt) > halfWidth || Math.abs(upd) > halfHeight) continue;

            DamageSources.ignoreNextKnockback(target);
            SpellDamageSource src = SpellDamageSource.source(owner, damageCategory, element())
                .withSpellType(com.github.runicrebirth.spells.types.MagicBlast.ID);
            DamageSources.applyDamage(target, damage, src);
            target.setDeltaMovement(target.getDeltaMovement().add(
                knockbackDir.x * 1.5, knockbackDir.y * 1.25, knockbackDir.z * 1.5));
            target.hurtMarked = true;
        }

        Vec3 center = pos.add(forward.scale(length / 2.0)).add(0.0f, 0.5f, 0.0f);
        ParticleHelper.burstParticleEvent(server, element().particle(), center,
            (int) (18 * size), 0.4 * size, 0.4 * size, 0.4 * size, 0.1, 1.0f);
    }

    @Override
    protected void spawnActiveParticles() {
//        Vec3 pos = this.position();
//        for (int i = 0; i < 3; i++) {
//            double dx = (this.level().getRandom().nextDouble() - 0.5) * size;
//            double dy = (this.level().getRandom().nextDouble() - 0.5) * size;
//            double dz = (this.level().getRandom().nextDouble() - 0.5) * size;
//            this.level().addParticle(element().particle(),
//                pos.x + dx, pos.y + dy, pos.z + dz,
//                0, 0.02, 0);
//        }
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
