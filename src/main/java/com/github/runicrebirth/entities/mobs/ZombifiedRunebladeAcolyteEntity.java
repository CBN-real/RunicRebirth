package com.github.runicrebirth.entities.mobs;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
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
import net.tslat.smartbrainlib.example.SBLSkeleton;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

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
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends ZombifiedRunebladeAcolyteEntity>> getSensors() {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedRunebladeAcolyteEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new LookAtTarget<>(), new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedRunebladeAcolyteEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<SBLSkeleton>(
                new TargetOrRetaliate<>()
                    .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                    .attackablePredicate(target -> target.isAlive() && (this == target.getLastHurtMob() || ((target instanceof Player player) && !player.getAbilities().invulnerable))),
                new SetPlayerLookTarget<>(),
                new SetRandomLookTarget<>()),
            new OneRandomBehaviour<>(
                new SetRandomWalkTarget<>()
                    .speedModifier(1),
                new Idle<>() // Don't do anything for a bit
                    .runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedRunebladeAcolyteEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new SetWalkTargetToAttackTarget<>().closeEnoughDist((owner, target) -> 2),
            new AnimatableMeleeAttack<>(20)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(new AnimationController<>(this, "attack_controller", 0, state -> {
          if (this.swinging)
            return state.setAndContinue(SWING);

          state.getController().forceAnimationReset();

          return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
