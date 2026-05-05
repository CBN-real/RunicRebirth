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
import com.github.interactivemagic.util.Utils;
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

public class MagicHammerEntity extends ThrowableProjectile implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int HOVER_DELAY_TICKS = 60;
    private static final int MAX_LIFETIME_TICKS = 160;

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(MagicHammerEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(MagicHammerEntity.class, EntityDataSerializers.FLOAT);

    private int age;
    private boolean falling;
    private float directDamage = 10f;
    private float splashDamage = 5f;
    private float directRadius = 1.5f;
    private float splashRadius = 2.5f;
    private float size = 1f;
    private Element element;
    private MagicDamageType damageCategory = MagicDamageType.BLUNT;

    public MagicHammerEntity(EntityType<? extends MagicHammerEntity> type, Level level) {
        super(type, level);
    }

    public MagicHammerEntity(Level level, LivingEntity owner, SpellParams params,
                             float directDamage, float splashDamage) {
        super(ModEntities.MAGIC_HAMMER.get(), owner, level);
        this.directDamage = directDamage;
        this.splashDamage = splashDamage;
        this.directRadius = 1.5f * params.size;
        this.splashRadius = 2.5f * params.size;
        this.size = params.size;
        this.element = params.element;
        this.damageCategory = params.damageCategory;
        this.entityData.set(DATA_ELEMENT, params.element.id().toString());
        this.entityData.set(DATA_SIZE, params.size);
    }

    @Override
    protected double getDefaultGravity() {
        return falling ? 0.08 : 0.0;
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
            if (!falling && this.age >= HOVER_DELAY_TICKS) {
                falling = true;
                this.setDeltaMovement(0, -0.5, 0);
            }
            if (!falling) {
                this.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            Vec3 pos = this.position();
            this.level().addParticle(ModParticles.FIRE_ELEMENT.get(), pos.x, pos.y + 0.3, pos.z, 0.0, 0.05, 0.0);
        }
        super.tick();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide || !falling) return;
        impact(result.getLocation());
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide || !falling) return;
        impact(Vec3.atCenterOf(result.getBlockPos()));
        this.discard();
    }

    private void impact(Vec3 center) {
        if (!(this.level() instanceof ServerLevel server)) return;
        Entity owner = this.getOwner();

        for (LivingEntity target : Utils.entitiesInRange(server, center, directRadius, this)) {
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
                DamageSources.applyDamage(target, directDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), directDamage);
            }
        }

        for (LivingEntity target : Utils.entitiesInRange(server, center, splashRadius, this)) {
            if (owner instanceof LivingEntity living) {
                SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
                DamageSources.applyDamage(target, splashDamage, source);
            } else {
                target.hurt(this.damageSources().magic(), splashDamage);
            }
        }

        int count = (int) (50 * size);
        server.sendParticles(element().particle(), center.x, center.y, center.z, count,
            0.6 * size, 0.6 * size, 0.6 * size, 0.12);
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
