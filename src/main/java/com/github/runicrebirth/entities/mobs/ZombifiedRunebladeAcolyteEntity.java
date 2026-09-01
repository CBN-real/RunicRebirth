package com.github.runicrebirth.entities.mobs;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
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

public class ZombifiedRunebladeAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<ZombifiedRunebladeAcolyteEntity> {

    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ZombifiedRunebladeAcolyteEntity(EntityType<? extends ZombifiedRunebladeAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.26)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 16.0)
            .add(Attributes.ATTACK_SPEED, 1.6);
    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(ZombifiedRunebladeAcolyteEntity entity) {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public ActivityBuilder<ZombifiedRunebladeAcolyteEntity> getCoreBehaviourGroup(ZombifiedRunebladeAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedRunebladeAcolyteEntity>create(Activity.CORE).behaviours(new LookAtTarget<>(), new MoveToWalkTarget<>());
    }

    @Override
    public ActivityBuilder<ZombifiedRunebladeAcolyteEntity> getIdleBehaviourGroup(ZombifiedRunebladeAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedRunebladeAcolyteEntity>create(Activity.IDLE).behaviours(
            new FirstApplicableBehaviour<ZombifiedRunebladeAcolyteEntity>(
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
    public ActivityBuilder<ZombifiedRunebladeAcolyteEntity> getFightingBehaviourGroup(ZombifiedRunebladeAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedRunebladeAcolyteEntity>create(Activity.FIGHT).behaviours(
            new InvalidateAttackTarget<>(),
            new SetWalkTargetToAttackTarget<>().closeEnoughDist((owner, target) -> 2),
            new AnimatableMeleeAttack<>(20)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<ZombifiedRunebladeAcolyteEntity>("attack_controller", 0, state -> {
          if (this.swinging)
            return state.setAndContinue(SWING);

          state.controller().reset();

          return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
