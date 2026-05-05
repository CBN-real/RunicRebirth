package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
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
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicBallistaEntity extends ThrowableProjectile implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int CHARGE_TICKS = 20;
    private static final int PIN_TICKS = 30;
    private static final int MAX_LIFETIME_TICKS = 200;

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(MagicBallistaEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(MagicBallistaEntity.class, EntityDataSerializers.FLOAT);

    private enum Phase { CHARGING, FLYING, PINNING }

    private int age;
    private int pinAge;
    private Phase phase = Phase.CHARGING;
    private Vec3 storedDirection;
    private float storedSpeed;
    private float damage = 8f;
    private float size = 1f;
    private Element element;
    private MagicDamageType damageCategory = MagicDamageType.SHARP;
    private Entity pinnedEntity;
    private Vec3 pinPosition;

    public MagicBallistaEntity(EntityType<? extends MagicBallistaEntity> type, Level level) {
        super(type, level);
    }

    public MagicBallistaEntity(Level level, LivingEntity owner, SpellParams params,
                               Vec3 direction, float speed) {
        super(ModEntities.MAGIC_BALLISTA.get(), owner, level);
        this.damage = params.damage;
        this.size = params.size;
        this.element = params.element;
        this.damageCategory = params.damageCategory;
        this.storedDirection = direction.normalize();
        this.storedSpeed = speed;
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
        this.entityData.set(DATA_SIZE, params.size);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
        builder.define(DATA_SIZE, 1f);
    }

    public String getElementId() {
        return this.entityData.get(DATA_ELEMENT);
    }

    public float getProjectileSize() {
        return this.entityData.get(DATA_SIZE);
    }

    private Element element() {
        if (this.level().isClientSide) {
            Element resolved = ElementRegistry.get(ResourceLocation.parse(this.entityData.get(DATA_ELEMENT)));
            return resolved != null ? resolved : ModElements.ARCANE.get();
        }
        return element != null ? element : ModElements.ARCANE.get();
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            ++this.age;
            if (this.age > MAX_LIFETIME_TICKS) {
                this.discard();
                return;
            }

            switch (phase) {
                case CHARGING -> {
                    this.setDeltaMovement(Vec3.ZERO);
                    if (this.age >= CHARGE_TICKS) {
                        phase = Phase.FLYING;
                        if (storedDirection != null) {
                            this.shoot(storedDirection.x, storedDirection.y, storedDirection.z, storedSpeed, 0.0F);
                        }
                    }
                }
                case FLYING -> {
                    // handled by super.tick() projectile movement
                }
                case PINNING -> {
                    ++pinAge;
                    this.setDeltaMovement(Vec3.ZERO);
                    if (pinPosition != null) {
                        this.setPos(pinPosition.x, pinPosition.y, pinPosition.z);
                    }
                    if (pinnedEntity != null && pinnedEntity.isAlive()) {
                        pinnedEntity.teleportTo(pinPosition.x, pinPosition.y, pinPosition.z);
                        pinnedEntity.setDeltaMovement(Vec3.ZERO);
                        pinnedEntity.hurtMarked = true;
                    }
                    if (pinAge >= PIN_TICKS) {
                        burstParticles();
                        this.discard();
                        return;
                    }
                }
            }
        } else {
            Vec3 pos = this.position();
            this.level().addParticle(ModParticles.FIRE_ELEMENT.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || phase != Phase.FLYING) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
            DamageSources.applyDamage(target, damage, source);
        } else {
            target.hurt(this.damageSources().magic(), damage);
        }

        phase = Phase.PINNING;
        pinnedEntity = target;
        Vec3 hitLoc = result.getLocation();
        Vec3 dir = this.getDeltaMovement().normalize();
        pinPosition = hitLoc.add(dir.scale(target.getBbWidth() * 0.5));
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(pinPosition.x, pinPosition.y, pinPosition.z);

        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(element().particle(), hitLoc.x, hitLoc.y, hitLoc.z, 10, 0.2, 0.2, 0.2, 0.03);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || phase != Phase.FLYING) return;
        burstParticles();
        this.discard();
    }

    private void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        int count = (int) (18 * size);
        server.sendParticles(element().particle(), pos.x, pos.y, pos.z, count, 0.3 * size, 0.3 * size, 0.3 * size, 0.05);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> {
            state.setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
