package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.init.ModElements;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MagicShieldEntity extends AbstractSpellEntity {

    private static final EntityDataAccessor<Float> DATA_SHIELD_HEALTH =
        SynchedEntityData.defineId(MagicShieldEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
        SynchedEntityData.defineId(MagicShieldEntity.class, EntityDataSerializers.INT);

    private UUID ownerUUID;
    private int maxDuration = 600;



    public MagicShieldEntity(EntityType<? extends MagicShieldEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 0;
        this.endTicks = 60;
    }

    public MagicShieldEntity(Level level, LivingEntity owner, SpellParams params,
                             float shieldHealth, int duration) {
        this(ModEntities.MAGIC_SHIELD.get(), level);
        this.ownerUUID = owner.getUUID();
        this.maxDuration = duration;
        initFromParams(params);
        this.entityData.set(DATA_SHIELD_HEALTH, shieldHealth);
        this.entityData.set(DATA_OWNER_ID, owner.getId());
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHIELD_HEALTH, 20f);
        builder.define(DATA_OWNER_ID, -1);
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
        return ownerUUID;
    }

    public int getOwnerId() {
        return this.entityData.get(DATA_OWNER_ID);
    }

    public void absorbDamage(float amount) {
        float health = getShieldHealth() - amount;
        setShieldHealth(health);
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
        if (ownerUUID == null) {
            beginEnding();
            return;
        }
        Entity owner = ((ServerLevel) this.level()).getEntity(ownerUUID);
        if (owner == null || !owner.isAlive()) {
            beginEnding();
            return;
        }
        snapToOwner(owner);
    }

    @Override
    protected void spawnActiveParticles() {
        int ownerId = this.entityData.get(DATA_OWNER_ID);
        if (ownerId != -1) {
            Entity owner = this.level().getEntity(ownerId);
            if (owner != null) {
                snapToOwner(owner);
            }
        }
        Vec3 pos = this.position();
        float sz = getShieldSize();
        for (int i = 0; i < 2; i++) {
            double angle = this.level().random.nextDouble() * Math.PI * 2;
            double dx = Math.cos(angle) * sz * 1.2;
            double dz = Math.sin(angle) * sz * 1.2;
            this.level().addParticle(ModParticles.FIRE_ELEMENT.get(),
                pos.x + dx, pos.y + 0.5 + this.level().random.nextDouble(), pos.z + dz,
                0.0, 0.02, 0.0);
        }
    }

    private void snapToOwner(Entity owner) {
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.xo = owner.xo;
        this.yo = owner.yo;
        this.zo = owner.zo;
    }

    @Override
    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        Element elem = ElementRegistry.get(ResourceLocation.parse(getElementId()));
        if (elem == null) elem = ModElements.ARCANE.get();
        server.sendParticles(elem.particle(), pos.x, pos.y + 1, pos.z, 30, 1.0, 1.0, 1.0, 0.05);
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

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }
}
