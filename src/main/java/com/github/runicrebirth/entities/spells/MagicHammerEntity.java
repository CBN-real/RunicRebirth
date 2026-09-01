package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.damage.DamageSources;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.util.ImpactHelper;
import com.github.runicrebirth.util.ParticleHelper;
import com.github.runicrebirth.util.Utils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import com.geckolib.animation.RawAnimation;


public class MagicHammerEntity extends AbstractInstantSpellEntity {

    private static final float HOVER_HEIGHT = 1.0f;

    private static final EntityDataAccessor<Integer> DATA_TRACKED_ENTITY_ID =
        SynchedEntityData.defineId(MagicHammerEntity.class, EntityDataSerializers.INT);

    private float directDamage = 10f;
    private float splashDamage = 5f;
    private float splashRadius = 2.5f;

    private UUID casterUUID;
    private Entity trackedEntity;
    private Vec3 targetBlockPos;

    public MagicHammerEntity(EntityType<? extends MagicHammerEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 72;
        this.endTicks = 47;
        this.end_spell = this.initiate_and_hold;
    }

    public MagicHammerEntity(Level level, LivingEntity caster, SpellParams params,
                             float directDamage, float splashDamage, Entity targetEntity) {
        this(ModEntities.MAGIC_HAMMER.get(), level);
        this.casterUUID = caster.getUUID();
        initFromParams(params);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.splashRadius = 2.5f * params.size;
        this.trackedEntity = targetEntity;
        this.entityData.set(DATA_TRACKED_ENTITY_ID, targetEntity.getId());
    }

    public MagicHammerEntity(Level level, LivingEntity caster, SpellParams params,
                             float directDamage, float splashDamage, Vec3 targetBlockPos) {
        this(ModEntities.MAGIC_HAMMER.get(), level);
        this.casterUUID = caster.getUUID();
        initFromParams(params);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.splashRadius = 2.5f * params.size;
        this.targetBlockPos = targetBlockPos;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TRACKED_ENTITY_ID, -1);
    }

    @Override
    protected void onChargingTick() {
        if (phaseAge == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_HAMMER_SPELL.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_HAMMER_INITIATE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        if (trackedEntity != null) {
            if (trackedEntity.isAlive()) {
                snapToTracked(trackedEntity);
            } else {
                targetBlockPos = trackedEntity.position();
                trackedEntity = null;
                this.entityData.set(DATA_TRACKED_ENTITY_ID, -1);
            }
        }
    }

    @Override
    protected void onActivated() {
        Vec3 impactPos;
        boolean hasDirectTarget = trackedEntity != null && trackedEntity.isAlive();
        if (hasDirectTarget) {
            impactPos = trackedEntity.position();
        } else if (targetBlockPos != null) {
            impactPos = targetBlockPos;
        } else {
            beginEnding();
            return;
        }
        impact(impactPos, hasDirectTarget);
        beginEnding();
    }

    @Override
    protected void onActiveTick() {}

    @Override
    protected void spawnActiveParticles() {
        int trackedId = this.entityData.get(DATA_TRACKED_ENTITY_ID);
        if (trackedId != -1 && this.getPhase() != SpellPhase.ENDING) {
            Entity tracked = this.level().getEntity(trackedId);
            if (tracked != null) {
                snapToTracked(tracked);
            }
        }
        Vec3 pos = this.position();
        this.level().addParticle(element().particle(), pos.x, pos.y + 0.3, pos.z, 0.0, 0.05, 0.0);
    }

    private void snapToTracked(Entity target) {
        this.setPos(target.getX(), target.getY() + HOVER_HEIGHT, target.getZ());
        this.xo = target.xo;
        this.yo = target.yo + HOVER_HEIGHT;
        this.zo = target.zo;
    }

    private void impact(Vec3 center, boolean hasDirectTarget) {
        if (!(this.level() instanceof ServerLevel server)) return;
        Entity caster = server.getEntity(casterUUID);

        if (hasDirectTarget && trackedEntity instanceof LivingEntity target) {
            if (caster instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                    .withSpellType(com.github.runicrebirth.spells.types.MagicHammer.ID);
                DamageSources.applyDamage(target, directDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), directDamage);
            }
        }

        for (LivingEntity target : Utils.entitiesInRange(server, center, splashRadius, this)) {
            if (hasDirectTarget && target == trackedEntity) continue;
            if (caster instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element())
                    .withSpellType(com.github.runicrebirth.spells.types.MagicHammer.ID);
                DamageSources.applyDamage(target, splashDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), splashDamage);
            }
        }

        ParticleHelper.burstParticleEvent(server, element().particle(1.5f), center,
            (int) (100 * size), 1.5 * size, 1.5 * size, 1.5 * size, 0.12, 1.0f);

        ImpactHelper.createImpact(server, center, splashRadius * 1.5f, element(), 2.0f);
    }


    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
