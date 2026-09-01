package com.github.runicrebirth.entities.mobs;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.entities.mobs.ai.CastSpellBehaviour;
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

public class ZombifiedArtificerAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<ZombifiedArtificerAcolyteEntity> {


    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack.hit");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("attack.cast");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ZombifiedArtificerAcolyteEntity(EntityType<? extends ZombifiedArtificerAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.ATTACK_DAMAGE, 2.5)
            .add(Attributes.FOLLOW_RANGE, 16.0)
            .add(Attributes.ATTACK_SPEED, 2.0);

    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(ZombifiedArtificerAcolyteEntity entity) {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public ActivityBuilder<ZombifiedArtificerAcolyteEntity> getCoreBehaviourGroup(ZombifiedArtificerAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedArtificerAcolyteEntity>create(Activity.CORE).behaviours(new LookAtTarget<>(), new MoveToWalkTarget<>());
    }

    @Override
    public ActivityBuilder<ZombifiedArtificerAcolyteEntity> getIdleBehaviourGroup(ZombifiedArtificerAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedArtificerAcolyteEntity>create(Activity.IDLE).behaviours(
            new FirstApplicableBehaviour<ZombifiedArtificerAcolyteEntity>(
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
    public ActivityBuilder<ZombifiedArtificerAcolyteEntity> getFightingBehaviourGroup(ZombifiedArtificerAcolyteEntity entity) {
        return ActivityBuilder.<ZombifiedArtificerAcolyteEntity>create(Activity.FIGHT).behaviours(
            new InvalidateAttackTarget<>(),
            new SetWalkTargetToAttackTarget<>().closeEnoughDist((owner, target) -> 1),
            new CastSpellBehaviour<ZombifiedArtificerAcolyteEntity>(40) {
                @Override
                protected void performCast(ServerLevel level, ZombifiedArtificerAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    entity.triggerAnim("cast_controller", "cast");
                    SpellParams params = new SpellParams(5f, 1.0f,0.25f, 0.75f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.BLUNT);
                    params.damage = params.damage * (1.0f + params.size) / 2.0f;
                    ModSpellTypes.MAGIC_PROJECTILE.get().onCast(ctx, params);
                }
            },
            new AnimatableMeleeAttack<>(4)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<ZombifiedArtificerAcolyteEntity>("attack_controller", 0, state -> {
          if (this.swinging)
            return state.setAndContinue(ATTACK);

          state.controller().reset();

          return PlayState.STOP;
        }));
        controllers.add(new AnimationController<ZombifiedArtificerAcolyteEntity>("cast_controller", 2, state -> PlayState.STOP)
            .triggerableAnim("cast", CAST));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
