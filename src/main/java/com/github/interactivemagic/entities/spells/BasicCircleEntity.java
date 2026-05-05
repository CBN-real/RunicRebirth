package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.events.SpellPostCastEvent;
import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.capabilities.magic.MagicData;
import com.github.interactivemagic.init.ModElements;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.network.StackChangedS2CPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

public class BasicCircleEntity extends Entity implements GeoEntity {

    private static final int MAX_LIFETIME = 300;

    private static final int FINISH_TICKS = 40;

    private static final Vec3 EMITTER_N  = new Vec3( 0.0035, 0.874, 0.0625);
    private static final Vec3 EMITTER_SE = new Vec3(-0.305,  0.302, 0.0625);
    private static final Vec3 EMITTER_SW = new Vec3( 0.310,  0.302, 0.0625);

    private static final RawAnimation INITIATE_SPELL = RawAnimation.begin().thenPlay("initiate_spell").thenLoop("hold_spell");
    private static final RawAnimation END_SPELL = RawAnimation.begin().thenPlayAndHold("end_spell");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<String> DATA_SPELL_TYPE_ID =
        SynchedEntityData.defineId(BasicCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_MODIFIER_IDS =
        SynchedEntityData.defineId(BasicCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(BasicCircleEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_FINISHING =
        SynchedEntityData.defineId(BasicCircleEntity.class, EntityDataSerializers.BOOLEAN);

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

    public BasicCircleEntity(EntityType<? extends BasicCircleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public BasicCircleEntity(Level level, ServerPlayer caster, SpellType spellType,
                             SpellParams params, Vec3 aimDirection, ItemStack wandItem,
                             int totalCasts, int castingDelayTicks) {
        this(ModEntities.BASIC_CIRCLE.get(), level);
        this.caster = caster;
        this.spellType = spellType;
        this.params = params;
        this.aimDirection = aimDirection.normalize();
        this.wandItem = wandItem.copy();
        this.castingDelayTicks = castingDelayTicks;
        this.ticksUntilNextCast = castingDelayTicks;
        this.remainingCasts = totalCasts;

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
    }

    public String getSpellTypeId() {
        return this.entityData.get(DATA_SPELL_TYPE_ID);
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
            spawnEmitterParticles();
            return;
        }

        if (entityData.get(DATA_FINISHING)) {
            if (--finishingTicks <= 0) {
                discard();
            }
            return;
        }

        age++;
        if (age > MAX_LIFETIME || caster == null || caster.isRemoved() || !caster.isAlive()) {
            beginFinishing();
            return;
        }

        ticksUntilNextCast--;
        if (ticksUntilNextCast <= 0 && remainingCasts > 0) {
            fireCast();
            remainingCasts--;
            if (remainingCasts > 0) {
                ticksUntilNextCast = castingDelayTicks;
            } else {
                beginFinishing();
            }
        }
    }

    private void beginFinishing() {
        cleanup();
        finishingTicks = FINISH_TICKS;
        entityData.set(DATA_FINISHING, true);
    }

    private void fireCast() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        SpellCastContext ctx = new SpellCastContext(serverLevel, caster, wandItem, position(), aimDirection);
        spellType.onCast(ctx, params);
        NeoForge.EVENT_BUS.post(new SpellPostCastEvent(ctx, spellType, params));
    }

    private void cleanup() {
        if (caster == null || caster.isRemoved()) return;
        MagicData data = MagicData.of(caster);
        data.setCastingInProgress(false);
        StackChangedS2CPacket.sendTo(caster);
    }

    private void spawnEmitterParticles() {
        if (tickCount < 15 || tickCount > 40) return;

        Element elem = ElementRegistry.get(ResourceLocation.parse(entityData.get(DATA_ELEMENT)));
        if (elem == null) elem = ModElements.ARCANE.get();
        ParticleOptions particle = elem.particle();

        spawnAtEmitter(EMITTER_N, particle);
        spawnAtEmitter(EMITTER_SE, particle);
        spawnAtEmitter(EMITTER_SW, particle);
    }

    private void spawnAtEmitter(Vec3 localOffset, ParticleOptions particle) {
        float xRad = (float) Math.toRadians(getXRot());
        float yRad = (float) Math.toRadians(-getYRot());
        Vec3 world = position().add(localOffset.xRot(xRad).yRot(yRad));
        level().addParticle(particle, world.x, world.y, world.z, 0, 0.02, 0);
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
            if (state.getAnimatable().entityData.get(DATA_FINISHING)) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_SPELL);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
