package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractInstantSpellEntity extends Entity implements GeoEntity {

    protected RawAnimation initiate_and_hold = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    protected static final RawAnimation HOLD_SPELL = RawAnimation.begin().thenLoop("hold_spell");
    protected RawAnimation end_spell = RawAnimation.begin().thenPlayAndHold("end_spell");
    private static final RawAnimation BOOK_LOOP = RawAnimation.begin().thenLoop("initiate_spell");

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractInstantSpellEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(AbstractInstantSpellEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(AbstractInstantSpellEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long displayStartNanos;

    protected int age;
    protected float damage = 1f;
    protected float size = 1f;
    protected Element element;
    protected MagicDamageType damageCategory = MagicDamageType.BLUNT;
    protected int chargeTicks = 10;
    protected int endTicks = 10;
    protected int phaseAge;

    protected AbstractInstantSpellEntity(EntityType<? extends AbstractInstantSpellEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
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
        super.tick();
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
                }
            }
        } else {
            spawnActiveParticles();
        }
    }

    protected void beginEnding() {
        if (getPhase() == SpellPhase.ENDING) return;
        setPhase(SpellPhase.ENDING);
    }

    protected void onChargingTick() {}
    protected void onActivated() {}
    protected abstract void onActiveTick();
    protected void onEndingTick() {}
    protected void spawnActiveParticles() {
        ParticleHelper.areaParticleEvent(this.level(), element().particle(), this.position(), 3.0, 1, this.size);
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
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spell_phase", 0, state -> {
            if (!this.isAddedToLevel()) {
                state.setAnimation(BOOK_LOOP);
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().getPhase() == SpellPhase.ENDING) {
                state.setAnimation(end_spell);
            } else {
                state.setAnimation(initiate_and_hold);
            }
            return PlayState.CONTINUE;
        }));
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
