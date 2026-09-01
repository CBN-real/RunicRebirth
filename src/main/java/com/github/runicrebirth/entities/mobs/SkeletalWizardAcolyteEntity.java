package com.github.runicrebirth.entities.mobs;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.entities.mobs.ai.CastSpellBehaviour;
import com.github.runicrebirth.entities.spells.MagicBlastEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
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
import net.minecraft.world.phys.Vec3;
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

public class SkeletalWizardAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<SkeletalWizardAcolyteEntity> {

    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("attack.cast");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SkeletalWizardAcolyteEntity(EntityType<? extends SkeletalWizardAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.15)
            .add(Attributes.FOLLOW_RANGE, 20.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(SkeletalWizardAcolyteEntity entity) {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public ActivityBuilder<SkeletalWizardAcolyteEntity> getCoreBehaviourGroup(SkeletalWizardAcolyteEntity entity) {
        return ActivityBuilder.<SkeletalWizardAcolyteEntity>create(Activity.CORE).behaviours(
            new LookAtTarget<>(),
            new StrafeTarget<>(),
            new MoveToWalkTarget<>());
    }

    @Override
    public ActivityBuilder<SkeletalWizardAcolyteEntity> getIdleBehaviourGroup(SkeletalWizardAcolyteEntity entity) {
      return ActivityBuilder.<SkeletalWizardAcolyteEntity>create(Activity.IDLE).behaviours(
          new FirstApplicableBehaviour<SkeletalWizardAcolyteEntity>(
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
    public ActivityBuilder<SkeletalWizardAcolyteEntity> getFightingBehaviourGroup(SkeletalWizardAcolyteEntity entity) {
        return ActivityBuilder.<SkeletalWizardAcolyteEntity>create(Activity.FIGHT).behaviours(
            new InvalidateAttackTarget<>(),
            new CastSpellBehaviour<SkeletalWizardAcolyteEntity>(40) {
                @Override
                protected boolean checkExtraStartConditions(ServerLevel level, SkeletalWizardAcolyteEntity entity) {
                    if (!super.checkExtraStartConditions(level, entity)) return false;
                    return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                        .map(t -> entity.distanceTo(t) <= 3.0f).orElse(false);
                }
                @Override
                protected void performCast(ServerLevel level, SkeletalWizardAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    entity.triggerAnim("cast_controller", "cast");
                    Vec3 spawnPos = entity.position().add(entity.getLookAngle().scale(1.0));
                    MagicBlastEntity blast = new MagicBlastEntity(ModEntities.MAGIC_BLAST.get(), level);
                    SpellParams params = new SpellParams(2.5f, 1.0f,1.0f, 1.0f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.BLUNT);
                    blast.init(entity, spawnPos, entity.getLookAngle(), params);
                    level.addFreshEntity(blast);
                }
            },
            new CastSpellBehaviour<SkeletalWizardAcolyteEntity>(60) {
                @Override
                protected boolean checkExtraStartConditions(ServerLevel level, SkeletalWizardAcolyteEntity entity) {
                    if (!super.checkExtraStartConditions(level, entity)) return false;
                    return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                        .map(t -> entity.distanceTo(t) > 3.0f).orElse(true);
                }
                @Override
                protected void performCast(ServerLevel level, SkeletalWizardAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    entity.triggerAnim("cast_controller", "cast");
                    SpellParams params = new SpellParams(6f, 1.5f,0.125f, 1.5f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.SHARP);
                    ModSpellTypes.MAGIC_ARROW.get().onCast(ctx, params);
                }
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<SkeletalWizardAcolyteEntity>("cast_controller", 2, state -> PlayState.STOP)
            .triggerableAnim("cast", CAST));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
