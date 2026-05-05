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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MagicBindingEntity extends Entity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int BIND_DURATION_TICKS = 60;

    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(MagicBindingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(MagicBindingEntity.class, EntityDataSerializers.STRING);

    private UUID casterUUID;
    private UUID boundEntityUUID;
    private Vec3 bindPosition;
    private int age;
    private float totalDamage;
    private float size = 1f;
    private Element element;
    private MagicDamageType damageCategory = MagicDamageType.SPIRIT;

    public MagicBindingEntity(EntityType<? extends MagicBindingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MagicBindingEntity(Level level, LivingEntity caster, LivingEntity target, SpellParams params) {
        this(ModEntities.MAGIC_BINDING.get(), level);
        this.casterUUID = caster.getUUID();
        this.boundEntityUUID = target.getUUID();
        this.bindPosition = target.position();
        this.totalDamage = params.damage;
        this.size = params.size;
        this.element = params.element;
        this.damageCategory = params.damageCategory;
        this.entityData.set(DATA_SIZE, params.size);
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
        this.setPos(target.getX(), target.getY(), target.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SIZE, 1f);
        builder.define(DATA_ELEMENT, ModElements.ARCANE.getId().toString());
    }

    public float getProjectileSize() {
        return this.entityData.get(DATA_SIZE);
    }

    public String getElementId() {
        return this.entityData.get(DATA_ELEMENT);
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
        super.tick();
        if (!this.level().isClientSide) {
            if (!(this.level() instanceof ServerLevel server)) return;
            ++this.age;
            if (this.age > BIND_DURATION_TICKS) {
                burstParticles();
                this.discard();
                return;
            }

            Entity bound = server.getEntity(boundEntityUUID);
            if (bound == null || !bound.isAlive()) {
                this.discard();
                return;
            }

            bound.teleportTo(bindPosition.x, bindPosition.y, bindPosition.z);
            bound.setDeltaMovement(Vec3.ZERO);
            bound.hurtMarked = true;
            this.setPos(bindPosition.x, bindPosition.y, bindPosition.z);

            if (this.age % 3 == 0 && bound instanceof LivingEntity living) {
                float tickDamage = totalDamage / (BIND_DURATION_TICKS / 3f);
                Entity caster = server.getEntity(casterUUID);
                if (caster instanceof LivingEntity casterLiving) {
                    SpellDamageSource source = SpellDamageSource.source(this, casterLiving, damageCategory, element())
                        .setIFrames(0);
                    DamageSources.ignoreNextKnockback(living);
                    DamageSources.applyDamage(living, tickDamage, source);
                } else {
                    DamageSources.ignoreNextKnockback(living);
                    living.hurt(this.damageSources().magic(), tickDamage);
                }
            }
        } else {
            Vec3 pos = this.position();
            float sz = getProjectileSize();
            double angle = this.level().random.nextDouble() * Math.PI * 2;
            double dx = Math.cos(angle) * sz * 0.5;
            double dz = Math.sin(angle) * sz * 0.5;
            this.level().addParticle(ModParticles.FIRE_ELEMENT.get(),
                pos.x + dx, pos.y + 0.5 + this.level().random.nextDouble() * 1.5, pos.z + dz,
                0.0, 0.01, 0.0);
        }
    }

    private void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        server.sendParticles(element().particle(), pos.x, pos.y + 1, pos.z, 20, 0.5, 0.8, 0.5, 0.03);
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
