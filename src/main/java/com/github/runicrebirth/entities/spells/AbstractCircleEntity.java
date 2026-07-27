package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.events.SpellPostCastEvent;
import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import com.github.runicrebirth.util.Log;
import com.github.runicrebirth.util.RaycastTarget;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractCircleEntity extends Entity implements GeoEntity {

    private static final int FINISH_TICKS = 20;

    private static final RawAnimation INITIATE_SPELL = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    private static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");
    private static final RawAnimation CODEX_INTRO = RawAnimation.begin().thenPlay("initiate_spell").thenPlay("hold_spell");
    private static final RawAnimation CODEX_END = RawAnimation.begin().thenPlayAndHold("end_spell");
    private static final int CODEX_INITIATE_TICKS = 40;
    private static final int CODEX_HOLD_TICKS = 60;
    private static final int CODEX_END_TICKS = 40;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long displayStartNanos;
    private int displayPhase;
    private double holdStartTick;
    private double phaseStartTick;
    private boolean phaseAnimSet;

    private static final EntityDataAccessor<String> DATA_SPELL_TYPE_ID =
        SynchedEntityData.defineId(AbstractCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_MODIFIER_IDS =
        SynchedEntityData.defineId(AbstractCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(AbstractCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_FINISHING =
        SynchedEntityData.defineId(AbstractCircleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ENTITY_ID =
        SynchedEntityData.defineId(AbstractCircleEntity.class, EntityDataSerializers.INT);

    private static final double MAX_TRACK_RANGE_SQR = 64.0 * 64.0;

    private ServerPlayer caster;
    private SpellType spellType;
    private SpellParams params;
    private Vec3 aimDirection;
    private ItemStack wandItem;
    private int castingDelayTicks;
    private int ticksUntilNextCast;
    private int remainingCasts;
    private int age;
    private int finishingTicks;
    private int lifespan;
    private float spellXRot;
    private float spellYRot;
    private Entity trackedEntity;
    private Vec3 targetBlockPos;

    protected AbstractCircleEntity(EntityType<? extends AbstractCircleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    protected AbstractCircleEntity(EntityType<? extends AbstractCircleEntity> type, Level level,
                                ServerPlayer caster, SpellType spellType, SpellParams params,
                                Vec3 aimDirection, ItemStack wandItem,
                                int totalCasts, int castingDelayTicks, int lifespan,
                                float xRot, float yRot, RaycastTarget target) {
        this(type, level);
        this.caster = caster;
        this.spellType = spellType;
        this.params = params;
        this.aimDirection = aimDirection.normalize();
        this.wandItem = wandItem.copy();
        this.castingDelayTicks = castingDelayTicks;
        this.ticksUntilNextCast = castingDelayTicks;
        this.remainingCasts = totalCasts;
        this.lifespan = Math.max(lifespan, castingDelayTicks + FINISH_TICKS + 20);
        this.spellXRot = xRot;
        this.spellYRot = yRot;

        if (target.hasEntityTarget()) {
            this.trackedEntity = target.entity();
            this.entityData.set(DATA_TARGET_ENTITY_ID, target.entity().getId());
        } else if (target.hasBlockTarget()) {
            this.targetBlockPos = target.blockPosition();
        }

        this.entityData.set(DATA_SPELL_TYPE_ID, spellType.id().getPath());
        this.entityData.set(DATA_MODIFIER_IDS, String.join(",", params.modifierIds));
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SPELL_TYPE_ID, "");
        builder.define(DATA_MODIFIER_IDS, "");
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
        builder.define(DATA_FINISHING, false);
        builder.define(DATA_TARGET_ENTITY_ID, -1);
    }

    public int getTargetEntityId() {
        return this.entityData.get(DATA_TARGET_ENTITY_ID);
    }

    public String getSpellTypeId() {
        return this.entityData.get(DATA_SPELL_TYPE_ID);
    }

    public String getElementId() {
        if (!this.isAddedToLevel()) {
            String override = BookDisplayState.getSelectedElement();
            if (override != null) return override;
        }
        return this.entityData.get(DATA_ELEMENT);
    }

    public String getModifierIds() {
        return this.entityData.get(DATA_MODIFIER_IDS);
    }

    public boolean hasModifier(String modPath) {
        String mods = getModifierIds();
        if (mods.isEmpty()) return false;
        for (String id : mods.split(",")) {
            if (id.equals(modPath)) return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (entityData.get(DATA_FINISHING)) {
            if (--finishingTicks <= 0) {
                discard();
            }
            return;
        }

        age++;
        if (age == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_SPAWN_CIRCLE.get(), SoundSource.PLAYERS, 0.75f, 1.0f);
        }
        if (age == 5) {
            spawnCrackling((ServerLevel) level());
        }
        if (age >= lifespan || caster == null || caster.isRemoved() || !caster.isAlive()) {
            beginFinishing();
            return;
        }

        if (ticksUntilNextCast > 0) {
          updateTracking();
        }

        ticksUntilNextCast--;
        if (ticksUntilNextCast <= 0 && remainingCasts > 0) {
            fireCast();
            remainingCasts--;
            if (remainingCasts > 0) {
                ticksUntilNextCast = castingDelayTicks;
            }
        }
    }

    private void spawnCrackling(ServerLevel serverLevel) {
        float radius = getCircleScale() * 0.15f;
        int color = params.element.displayColor();
        EnergyCracklingEntity crackling = new EnergyCracklingEntity(serverLevel, radius, color, lifespan - 10, 0.5f, 1.0f, 0.15f * getCircleScale());
        var center = getBoundingBox().getCenter();
        crackling.setPos(center.x, center.y, center.z);
        crackling.attachTo(this);
        serverLevel.addFreshEntity(crackling);
    }

    private void beginFinishing() {
        cleanup();
        finishingTicks = FINISH_TICKS;
        entityData.set(DATA_FINISHING, true);
    }

    private void updateTracking() {
        if (trackedEntity == null) return;

        if (trackedEntity.isRemoved() || !trackedEntity.isAlive()
                || position().distanceToSqr(trackedEntity.position()) > MAX_TRACK_RANGE_SQR) {
            trackedEntity = null;
            this.entityData.set(DATA_TARGET_ENTITY_ID, -1);
            return;
        }



        Vec3 entityCenter = trackedEntity.getBoundingBox().getCenter();
        Vec3 toTarget = entityCenter.subtract(position());
        if (toTarget.lengthSqr() < 1.0E-6) return;

        aimDirection = toTarget.normalize();
        double dx = toTarget.x;
        double dz = toTarget.z;
        float newYRot = (float) Math.toDegrees(Math.atan2(-dx, dz));
        double hDist = Math.sqrt(dx * dx + dz * dz);
        float newXRot = (float) Math.toDegrees(Math.atan2(-toTarget.y, hDist));
        setYRot(newYRot);
        setXRot(newXRot);
        spellXRot = newXRot;
        spellYRot = newYRot;
        if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info(
            String.format("[RunicRebirth] Tracking entity: xRot=%.3f, yRot=%.3f, x=%.3f, y=%.3f",
                newXRot, newYRot, entityCenter.x, entityCenter.y));
    }

    private float getCircleScale() {
        if (hasModifier("size_plus_four")) return 3.0f;
        if (hasModifier("size_plus_two")) return 2.0f;
        if (hasModifier("size_plus")) return 1.5f;
        return 1.0f;
    }

    private Vec3 getCircleCenterCast(float spellHeight) {
        float scale = getCircleScale();
        Vec3 localCenter = new Vec3(0, (getCircleHeight() - spellHeight) / 2.0f, 0);
        float xRad = (float) Math.toRadians(getXRot());
        float yRad = (float) Math.toRadians(-getYRot());
        return position().add(localCenter.xRot(xRad).yRot(yRad));
    }

    protected float getCircleHeight() {
        return 1.0f;
    }

    private void fireCast() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        SpellCastContext ctx = new SpellCastContext(serverLevel, caster, wandItem, getCircleCenterCast(spellType.spellHeight()), aimDirection, spellXRot, spellYRot,
            trackedEntity instanceof net.minecraft.world.entity.LivingEntity le ? le : null);
        spellType.onCast(ctx, params);
        NeoForge.EVENT_BUS.post(new SpellPostCastEvent(ctx, spellType, params));
    }

    private void cleanup() {
        if (caster == null || caster.isRemoved()) return;
        StackChangedS2CPacket.sendTo(caster);
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
        controllers.add(new AnimationController<>(this, "form", 0, state -> {
            if (!this.isAddedToLevel()) {
                double currentTick = this.getTick(this);
                var controller = state.getController();
                switch (displayPhase) {
                    case 0:
                        if (!phaseAnimSet) {
                            state.setAnimation(CODEX_INTRO);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > CODEX_INITIATE_TICKS) {
                            displayPhase = 1;
                            holdStartTick = currentTick;
                            phaseAnimSet = false;
                        }
                        break;
                    case 1:
                        if (!phaseAnimSet) {
                            phaseAnimSet = true;
                        }
                        if (currentTick - holdStartTick >= CODEX_HOLD_TICKS) {
                            displayPhase = 2;
                            phaseAnimSet = false;
                        }
                        break;
                    case 2:
                        if (!phaseAnimSet) {
                            state.setAnimation(CODEX_END);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > CODEX_END_TICKS) {
                            displayPhase = 0;
                            phaseAnimSet = false;
                            controller.forceAnimationReset();
                        }
                        break;
                }
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().entityData.get(DATA_FINISHING)) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_SPELL);
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
