package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicBlastEntity extends AbstractSpellEntity {

    private UUID ownerUUID;
    private Vec3 aimDirection = Vec3.ZERO;
    private double range = 6.0;
    private double halfAngle = 35.0;

    public MagicBlastEntity(EntityType<? extends MagicBlastEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 10;
        this.endTicks = 10;
    }

    public void init(LivingEntity owner, Vec3 spawnPos, Vec3 dir, SpellParams params) {
        this.ownerUUID = owner.getUUID();
        this.aimDirection = dir;
        this.range = params.size;
        initFromParams(params);
        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        float yaw = (float) (Mth.atan2(dir.x, dir.z) * (180.0 / Math.PI));
        float pitch = (float) (-(Mth.atan2(dir.y, dir.horizontalDistance()) * (180.0 / Math.PI)));
        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    @Override
    protected void onActivated() {
        if (!(this.level() instanceof ServerLevel server)) return;
        if (ownerUUID == null) {
            beginEnding();
            return;
        }
        LivingEntity owner = null;
        if (server.getEntity(ownerUUID) instanceof LivingEntity living) {
            owner = living;
        }
        Vec3 pos = this.position();
        Vec3 knockbackDir = aimDirection.normalize();
        for (LivingEntity target : Utils.entitiesInCone(server, pos, aimDirection, range, halfAngle, owner)) {
            SpellDamageSource src = SpellDamageSource.source(owner, damageCategory, element());
            DamageSources.applyDamage(target, damage, src);
            target.setDeltaMovement(target.getDeltaMovement().add(
                knockbackDir.x * 1.5, knockbackDir.y * 1.25, knockbackDir.z * 1.5));
            target.hurtMarked = true;
        }
        burstParticles();
        beginEnding();
    }

    @Override
    protected void onActiveTick() {
        if (getPhase() == SpellPhase.ACTIVE) {
            beginEnding();
        }
    }

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        for (int i = 0; i < 3; i++) {
            double dx = (this.level().random.nextDouble() - 0.5) * size;
            double dy = (this.level().random.nextDouble() - 0.5) * size;
            double dz = (this.level().random.nextDouble() - 0.5) * size;
            this.level().addParticle(element().particle(),
                pos.x + dx, pos.y + dy, pos.z + dz,
                0, 0.02, 0);
        }
    }
}
