package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.init.ModElements;
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
    private Vec3 storedInitialMovement;

    protected AbstractProjectileSpellEntity(EntityType<? extends AbstractProjectileSpellEntity> type, Level level) {
        super(type, level);
    }

    protected AbstractProjectileSpellEntity(EntityType<? extends AbstractProjectileSpellEntity> type,
                                            LivingEntity owner, Level level) {
        super(type, owner, level);
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
              Vec3 dm = getDeltaMovement();
                if (storedInitialMovement == null) {
                    if (dm.lengthSqr() > 0) {
                        storedInitialMovement = dm;
                    }
                }
                setDeltaMovement(dm.scale(0.1));
                phaseAge++;
                onChargingTick();
                if (phaseAge >= chargeTicks) {
                    setPhase(SpellPhase.ACTIVE);
                    if (storedInitialMovement != null) {
                        setDeltaMovement(storedInitialMovement);
                    }
                    onActivated();
                    phase = SpellPhase.ACTIVE;
                }
            }
            if (phase == SpellPhase.ACTIVE) {
                onActiveTick();
            } else if (phase == SpellPhase.ENDING) {
                phaseAge++;
                setDeltaMovement(Vec3.ZERO);
                onEndingTick();
                if (phaseAge >= endTicks) {
                    discard();
                    return;
                }
            }
        } else {
            spawnActiveParticles();
        }
        boolean stationary = getPhase() != SpellPhase.ACTIVE;
        float yR = this.getYRot();
        float xR = this.getXRot();
        float yRO = this.yRotO;
        float xRO = this.xRotO;
        super.tick();
        if (stationary) {
            this.setYRot(yR);
            this.setXRot(xR);
            this.yRotO = yRO;
            this.xRotO = xRO;
        }
    }

    protected void beginEnding() {
        if (getPhase() == SpellPhase.ENDING) return;
        setPhase(SpellPhase.ENDING);
        this.noPhysics = true;
        setDeltaMovement(Vec3.ZERO);
    }

    protected void onChargingTick() {}
    protected void onActivated() {}
    protected void onActiveTick() {}
    protected void onEndingTick() {}
    protected void spawnActiveParticles() {}

    protected void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        int count = (int) (18 * size);
        server.sendParticles(element().particle(), pos.x, pos.y, pos.z,
            count, 0.2 * size, 0.2 * size, 0.2 * size, 0.05);
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
