package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.ScaledSpellEntity;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractProjectileSpellEntity extends ThrowableProjectile implements GeoEntity, ScaledSpellEntity {

    protected static final RawAnimation INITIATE_AND_HOLD = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    protected static final RawAnimation HOLD_SPELL = RawAnimation.begin().thenLoop("hold_spell");
    protected static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");
    private static final RawAnimation SPELLBOOK_INTRO = RawAnimation.begin().thenPlay("initiate_spell").thenPlay("hold_spell");
    private static final RawAnimation SPELLBOOK_END = RawAnimation.begin().thenPlayAndHold("end_spell");
    protected static final int SPELLBOOK_HOLD_TICKS = 60;

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SPELL_HEIGHT =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long displayStartNanos;
    private int displayPhase;
    private double holdStartTick;
    private double phaseStartTick;
    private boolean phaseAnimSet;

    protected int age;
    protected float damage = 1f;
    protected float size = 1f;
    protected float spellHeight = 1f;
    protected Element element;
    protected MagicDamageType damageCategory = MagicDamageType.BLUNT;
    protected int chargeTicks = 41;
    protected int endTicks = 15;
    protected int phaseAge;
    protected LivingEntity trackingTarget;
    protected float homingStrength = 0.12f;
    private Vec3 storedDirection;
    private float storedSpeed;

    protected AbstractProjectileSpellEntity(EntityType<? extends AbstractProjectileSpellEntity> type, Level level) {
        super(type, level);
    }

    protected AbstractProjectileSpellEntity(EntityType<? extends AbstractProjectileSpellEntity> type,
        LivingEntity owner, Level level) {
      super(type, owner, level);

    }

    protected AbstractProjectileSpellEntity(EntityType<? extends AbstractProjectileSpellEntity> type,
                                            LivingEntity owner, Level level, Vec3 direction, float speed) {
        super(type, owner, level);
        this.storedDirection = direction;
        this.storedSpeed = speed;
        this.shoot(storedDirection.x, storedDirection.y, storedDirection.z, 0.001F, 0.0F);
        setYRot(owner.getYRot());
        setXRot(owner.getXRot());
    }

    protected void initFromParams(SpellParams params) {
        this.damage = params.damage;
        this.size = params.size;
        this.spellHeight = params.spellHeight;
        this.element = params.element;
        this.damageCategory = params.damageCategory;
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
        this.entityData.set(DATA_SIZE, params.size);
        this.entityData.set(DATA_SPELL_HEIGHT, params.spellHeight);
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(getHitWidth(), getHitHeight());
    }

    protected float getHitWidth() { return size; }
    protected float getHitHeight() { return size; }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
        builder.define(DATA_SIZE, 1f);
        builder.define(DATA_SPELL_HEIGHT, 1f);
        builder.define(DATA_PHASE, SpellPhase.CHARGING.ordinal());
        builder.define(DATA_TARGET_ID, -1);
    }

    public SpellPhase getPhase() {
        return SpellPhase.values()[this.entityData.get(DATA_PHASE)];
    }

    protected void setPhase(SpellPhase phase) {
        this.entityData.set(DATA_PHASE, phase.ordinal());
        this.phaseAge = 0;
    }

    protected Element element() {
        if (this.level().isClientSide) {
            Element resolved = ElementRegistry.get(ResourceLocation.parse(this.entityData.get(DATA_ELEMENT)));
            return resolved != null ? resolved : ModElements.ARCANE.get();
        }
        return element != null ? element : ModElements.ARCANE.get();
    }

    public String getElementId() {
        if (!this.isAddedToLevel()) {
            String override = BookDisplayState.getSelectedElement();
            if (override != null) return override;
        }
        return this.entityData.get(DATA_ELEMENT);
    }

    public float getProjectileSize() {
        return this.entityData.get(DATA_SIZE);
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            age++;
            SpellPhase phase = getPhase();
            if (phase == SpellPhase.CHARGING) {
                phaseAge++;
                onChargingTick();
                if (phaseAge >= chargeTicks) {
                    setPhase(SpellPhase.ACTIVE);
                    onActivated();
                    phase = SpellPhase.ACTIVE;
                }
            }
            if (phase == SpellPhase.ACTIVE) {
                onActiveTick();
                if (getPhase() == SpellPhase.ACTIVE) {
                    scanWideBoundingBoxHits();
                }
            } else if (phase == SpellPhase.ENDING) {
                phaseAge++;
                onEndingTick();
                if (phaseAge >= endTicks) {
                    discard();
                    return;
                }
            }
        } else {
            spawnActiveParticles();
            resolveClientTarget();
        }
        // Applied both sides so client steers its own extrapolated path, preventing position-correction jerk
        if (getPhase() == SpellPhase.ACTIVE) {
            applyHoming();
        }
        super.tick();
    }

    private void resolveClientTarget() {
        if (trackingTarget != null && trackingTarget.isAlive()) return;
        int id = this.entityData.get(DATA_TARGET_ID);
        if (id == -1) return;
        Entity e = this.level().getEntity(id);
        if (e instanceof LivingEntity le) trackingTarget = le;
    }

    protected void scanWideBoundingBoxHits() {
        Vec3 movement = this.getDeltaMovement();
        AABB swept = this.getBoundingBox().expandTowards(movement);

        for (Entity target : this.level().getEntities(this, swept, this::canHitEntity)) {
            onHitEntity(new EntityHitResult(target));
            if (getPhase() != SpellPhase.ACTIVE) return;
        }

        if (getPhase() == SpellPhase.ACTIVE && !this.level().noCollision(this, swept)) {
            Vec3 from = this.position();
            Vec3 to = from.add(movement);
            BlockHitResult blockHit = this.level().clip(
                new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (blockHit.getType() != HitResult.Type.MISS) {
                onHitBlock(blockHit);
            } else {
                BlockPos pos = BlockPos.containing(from);
                Direction dir = Direction.getNearest(-movement.x, -movement.y, -movement.z);
                onHitBlock(new BlockHitResult(from, dir, pos, false));
            }
        }
    }

    public void setTrackingTarget(LivingEntity target) {
        this.trackingTarget = target;
        this.entityData.set(DATA_TARGET_ID, target != null ? target.getId() : -1);
    }

    private void applyHoming() {
        if (trackingTarget == null || !trackingTarget.isAlive()) return;
        Vec3 toTarget = trackingTarget.getBoundingBox().getCenter().subtract(this.position());
        double dist = toTarget.length();
        if (dist < 1e-6) return;
        Vec3 toTargetDir = toTarget.scale(1.0 / dist);
        Vec3 current = this.getDeltaMovement();
        double speed = current.length();
        if (speed < 1e-6) return;
        // Taper off near target to prevent oscillation/jerkiness
        double strength = homingStrength * Math.min(1.0, dist / 3.0);
        Vec3 currentDir = current.normalize();
        Vec3 newDir = currentDir.add(toTargetDir.subtract(currentDir).scale(strength)).normalize();
        this.setDeltaMovement(newDir.scale(speed));
    }

    protected void beginEnding() {
        if (getPhase() == SpellPhase.ENDING) return;
        setPhase(SpellPhase.ENDING);
        this.noPhysics = true;
        setDeltaMovement(getDeltaMovement().scale(0.001));
    }

    protected void onChargingTick() {}
    protected void onActivated() {
      if (storedDirection != null) {
        this.shoot(storedDirection.x, storedDirection.y, storedDirection.z, this.storedSpeed, 0.0F);
      }
    }
    protected void onActiveTick() {}
    protected void onEndingTick() {}
    protected void spawnActiveParticles() {
        float h = this.entityData.get(DATA_SPELL_HEIGHT);
        Vec3 center = this.position().add(0, h * this.size / 2.0, 0);
        ParticleHelper.trailParticleEvent(this.level(), element().particle(), center, this.getDeltaMovement(), this.size);
    }

    protected void burstParticles() {
        burstParticles(1.0f);
    }

    protected void burstParticles(float scale) {
        if (!(this.level() instanceof ServerLevel server)) return;
        ParticleHelper.burstParticleEvent(server, element().particle(), this.position(),
            (int) (18 * size), 0.2 * size, 0.2 * size, 0.2 * size, 0.05, scale);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spell_phase", 0, state -> {
            if (!this.isAddedToLevel()) {
                double currentTick = this.getTick(this);
                var controller = state.getController();
                switch (displayPhase) {
                    case 0:
                        if (!phaseAnimSet) {
                            state.setAnimation(SPELLBOOK_INTRO);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > this.chargeTicks) {
                            displayPhase = 1;
                            holdStartTick = currentTick;
                            phaseAnimSet = false;
                        }
                        break;
                    case 1:
                        if (!phaseAnimSet) {
                            phaseAnimSet = true;
                        }
                        if (currentTick - holdStartTick >= SPELLBOOK_HOLD_TICKS) {
                            displayPhase = 2;
                            phaseAnimSet = false;
                        }
                        break;
                    case 2:
                        if (!phaseAnimSet) {
                            state.setAnimation(SPELLBOOK_END);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > this.endTicks) {
                            displayPhase = 0;
                            phaseAnimSet = false;
                            controller.forceAnimationReset();
                        }
                        break;
                }
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().getPhase() == SpellPhase.ENDING) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_AND_HOLD);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public double getTick(Object entity) {
        if (!this.isAddedToLevel()) {
            long now = System.nanoTime();
            if (displayStartNanos == 0L) displayStartNanos = now;
            return (now - displayStartNanos) / 50_000_000.0;
        }
        return this.tickCount;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
