package com.github.runicrebirth.entities.mobs;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.entities.mobs.ai.CastSpellBehaviour;
import com.github.runicrebirth.entities.mobs.ai.CircleAroundTargetBehaviour;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSpellTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.ActivityBuilder;
import net.tslat.smartbrainlib.api.internal.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.base.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.base.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SkeletalMageAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<SkeletalMageAcolyteEntity> {


    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("attack.cast");


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SkeletalMageAcolyteEntity(EntityType<? extends SkeletalMageAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 18.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(SkeletalMageAcolyteEntity entity) {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }



    @Override
    public ActivityBuilder<SkeletalMageAcolyteEntity> getCoreBehaviourGroup(SkeletalMageAcolyteEntity entity) {
        return ActivityBuilder.<SkeletalMageAcolyteEntity>create(Activity.CORE).behaviours(
            new LookAtTarget<>(),
            new StrafeTarget<>(),
            new MoveToWalkTarget<>());
    }

    @Override
    public ActivityBuilder<SkeletalMageAcolyteEntity> getIdleBehaviourGroup(SkeletalMageAcolyteEntity entity) {
      return ActivityBuilder.<SkeletalMageAcolyteEntity>create(Activity.IDLE).behaviours(
          new FirstApplicableBehaviour<SkeletalMageAcolyteEntity>(
              new TargetOrRetaliate<>()
                  .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                  .canRetaliateAgainst(target -> target.isAlive() && (this == target.getLastHurtMob() || ((target instanceof Player player) && !player.getAbilities().invulnerable))),
              new SetPlayerLookTarget<>(),
              new SetRandomLookTarget<>()),
          new OneRandomBehaviour<>(
              new SetRandomWalkTarget<>()
                  .speedModifier(1),
              new Idle<>()
                  .runFor(e -> e.getRandom().nextInt(30, 60))));
    }

    @Override
    public ActivityBuilder<SkeletalMageAcolyteEntity> getFightingBehaviourGroup(SkeletalMageAcolyteEntity entity) {
        return ActivityBuilder.<SkeletalMageAcolyteEntity>create(Activity.FIGHT).behaviours(
            new InvalidateAttackTarget<>(),
            new CastSpellBehaviour<SkeletalMageAcolyteEntity>(60) {
                @Override
                protected void performCast(ServerLevel level, SkeletalMageAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    entity.triggerAnim("cast_controller", "cast");
                    SpellParams params = new SpellParams(6f, 1.0f,0.125f, 1.5f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.SHARP);
                    ModSpellTypes.MAGIC_ARROW.get().onCast(ctx, params);
                }
            },
            new CastSpellBehaviour<SkeletalMageAcolyteEntity>(120) {
                {
                    cooldownTimer = 30;
                }
                @Override
                protected void performCast(ServerLevel level, SkeletalMageAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    entity.triggerAnim("cast_controller", "cast");
                    SpellParams params = new SpellParams(5f, 1.0f,0.25f, 0.75f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.BLUNT);
                    ModSpellTypes.MAGIC_PROJECTILE.get().onCast(ctx, params);
                }
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<SkeletalMageAcolyteEntity>("cast_controller", 2, state -> PlayState.STOP)
            .triggerableAnim("cast", CAST));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
