package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.particle.ScaledParticleOption;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicShieldEntity extends AbstractEffectSpellEntity {

    private static final EntityDataAccessor<Float> DATA_SHIELD_HEALTH =
        SynchedEntityData.defineId(MagicShieldEntity.class, EntityDataSerializers.FLOAT);

    public MagicShieldEntity(EntityType<? extends MagicShieldEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 30;
        this.endTicks = 60;
    }

    public MagicShieldEntity(Level level, LivingEntity owner, SpellParams params,
                             float shieldHealth, int duration) {
        this(ModEntities.MAGIC_SHIELD.get(), level);
        this.maxDuration = duration;
        initFromParams(params);
        setFollowedEntity(owner);
        this.entityData.set(DATA_SHIELD_HEALTH, shieldHealth);
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHIELD_HEALTH, 20f);
    }

    public float getShieldHealth() {
        return this.entityData.get(DATA_SHIELD_HEALTH);
    }

    public void setShieldHealth(float health) {
        this.entityData.set(DATA_SHIELD_HEALTH, health);
    }

    public float getShieldSize() {
        return 1.0f;
    }

    public UUID getOwnerUUID() {
        return followedUUID;
    }

    public int getOwnerId() {
        return getFollowedEntityId();
    }

    @Override
    protected void onChargingTick() {
        if (phaseAge == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_SHIELD_INITIATE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    protected void onEndingTick() {
        if (phaseAge == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_SHIELD_END.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public void absorbDamage(float amount) {
        float health = getShieldHealth() - amount;
        setShieldHealth(health);
        if (!this.level().isClientSide()) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_SHIELD_HIT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        if (health <= 0) {
            burstParticles();
            beginEnding();
        }
    }

    @Override
    protected void onActiveTick() {
        if (age > maxDuration) {
            burstParticles();
            beginEnding();
            return;
        }
        if (followedUUID == null) {
            beginEnding();
            return;
        }
        Entity owner = ((ServerLevel) this.level()).getEntity(followedUUID);
        if (owner == null || !owner.isAlive()) {
            beginEnding();
            return;
        }
        snapToFollowed(owner);
    }

    @Override
    protected void spawnActiveParticles() {
        int ownerId = getFollowedEntityId();
        if (ownerId != -1) {
            Entity owner = this.level().getEntity(ownerId);
            if (owner != null) {
                snapToFollowed(owner);
            }
        }
        Vec3 pos = this.position();
        float sz = getShieldSize();
        for (int i = 0; i < 2; i++) {
            double angle = this.level().random.nextDouble() * Math.PI * 2;
            double dx = Math.cos(angle) * sz * 1.2;
            double dz = Math.sin(angle) * sz * 1.2;
            this.level().addParticle(new ScaledParticleOption(ModParticles.FIRE_ELEMENT.get(), 1.0f),
                pos.x + dx, pos.y + 0.5 + this.level().random.nextDouble(), pos.z + dz,
                0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Element elem = ElementRegistry.get(ResourceLocation.parse(getElementId()));
        if (elem == null) elem = ModElements.ARCANE.get();
        ParticleHelper.burstParticleEvent(server, elem.particle(), this.position().add(0, 1, 0),
            30, 1.0, 1.0, 1.0, 0.05, 1.0f);
    }

    public static MagicShieldEntity getShieldForEntity(Level level, LivingEntity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(3.0);
        for (Entity e : level.getEntities(entity, searchBox)) {
            if (e instanceof MagicShieldEntity shield
                && entity.getUUID().equals(shield.getOwnerUUID())
                && shield.getShieldHealth() > 0) {
                return shield;
            }
        }
        return null;
    }
}
