package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractProjectileSpellEntity extends ThrowableProjectile implements GeoEntity {

    protected static final RawAnimation INITIATE_AND_HOLD = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    protected static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(AbstractProjectileSpellEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected int age;
    protected float damage = 1f;
    protected float size = 1f;
    protected Element element;
    protected MagicDamageType damageCategory = MagicDamageType.BLUNT;
    protected int chargeTicks = 10;
    protected int endTicks = 10;
    protected int phaseAge;
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
    }

    protected void initFromParams(SpellParams params) {
        this.damage = params.damage;
        this.size = params.size;
        this.element = params.element;
        this.damageCategory = params.damageCategory;
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
        builder.define(DATA_PHASE, SpellPhase.CHARGING.ordinal());
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
        }
        super.tick();

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
        ParticleHelper.trailParticleEvent(this.level(), element().particle(), this.position(), this.getDeltaMovement(), this.size);
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
            if (state.getAnimatable().getPhase() == SpellPhase.ENDING) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_AND_HOLD);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
