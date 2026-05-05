package com.github.interactivemagic.entities.spells;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
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

public class MagicProjectileEntity extends ThrowableProjectile implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int MAX_LIFETIME_TICKS = 80;

    private static final EntityDataAccessor<String> DATA_ELEMENT =
        SynchedEntityData.defineId(MagicProjectileEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_SIZE =
        SynchedEntityData.defineId(MagicProjectileEntity.class, EntityDataSerializers.FLOAT);

    private int age;
    private float damage = 1f;
    private float size = 1f;
    private Element element;
    private MagicDamageType damageCategory = MagicDamageType.BLUNT;

    public MagicProjectileEntity(EntityType<? extends MagicProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MagicProjectileEntity(Level level, LivingEntity owner, SpellParams params) {
        super(ModEntities.MAGIC_PROJECTILE.get(), owner, level);
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
    }

    private Element element() {
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
        super.tick();
        if (!this.level().isClientSide) {
            if (++this.age > MAX_LIFETIME_TICKS) {
                this.discard();
                return;
            }
        } else {
            Vec3 pos = this.position();
            this.level().addParticle(ModParticles.FIRE_ELEMENT.get(), pos.x, pos.y - 0.2, pos.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) return;
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            SpellDamageSource source = SpellDamageSource.source(this, living, damageCategory, element());
            DamageSources.applyDamage(target, damage, source);
        } else {
            target.hurt(this.damageSources().magic(), damage);
        }
        burstParticles();
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide) return;
        burstParticles();
        this.discard();
    }

    private void burstParticles() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 pos = this.position();
        int count = (int) (18 * size);
        server.sendParticles(element().particle(), pos.x, pos.y, pos.z, count, 0.2 * size, 0.2 * size, 0.2 * size, 0.05);
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
