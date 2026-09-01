package com.github.runicrebirth.entities.mobs;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.entities.mobs.ai.CastSpellBehaviour;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSpellTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.ActivityBuilder;
import net.tslat.smartbrainlib.api.internal.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.base.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.base.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
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

public class RunesteelGolemEntity extends Monster implements GeoEntity, SmartBrainOwner<RunesteelGolemEntity> {

    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation BEAM_CAST = RawAnimation.begin().thenPlay("attack.cast");
    private static final RawAnimation DIE = RawAnimation.begin().thenPlayAndHold("misc.die");
    private static final RawAnimation POWER_UP = RawAnimation.begin().thenPlay("misc.power_up");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunesteelGolemEntity(EntityType<? extends RunesteelGolemEntity> type, Level level) {
        super(type, level);
        this.swingTime = 30;
        this.updateSwingTime();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level) {
            private int recalcCooldown = 0;
            private BlockPos lastTarget = null;

            @Override
            public boolean moveTo(double x, double y, double z, double speed) {
                BlockPos target = BlockPos.containing(x, y, z);
                if (target.equals(lastTarget) && recalcCooldown-- > 0) return true;
                lastTarget = target;
                recalcCooldown = 20;
                return super.moveTo(x, y, z, speed);
            }
        };
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        triggerAnim("spawn_controller", "power_up");
        return data;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0)
            .add(Attributes.MOVEMENT_SPEED, 0.18)
            .add(Attributes.ATTACK_DAMAGE, 12.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(Attributes.ATTACK_SPEED, 0.6f);

    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 25) {
            this.discard();
        }
    }

    @Override
    public List<? extends ExtendedSensor<?>> getSensors(RunesteelGolemEntity entity) {
        return ObjectArrayList.of(
            new NearbyLivingEntitySensor<>(),
            new HurtBySensor<>()
        );
    }

    @Override
    public ActivityBuilder<RunesteelGolemEntity> getCoreBehaviourGroup(RunesteelGolemEntity entity) {
        return ActivityBuilder.<RunesteelGolemEntity>create(Activity.CORE).behaviours(
            new LookAtTarget<>(),
            //new StrafeTarget<>(),
            new MoveToWalkTarget<>()
        );
    }

    @Override
    public ActivityBuilder<RunesteelGolemEntity> getIdleBehaviourGroup(RunesteelGolemEntity entity) {
        return ActivityBuilder.<RunesteelGolemEntity>create(Activity.IDLE).behaviours(
            new FirstApplicableBehaviour<RunesteelGolemEntity>(
                new TargetOrRetaliate<>()
                    .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                    .canRetaliateAgainst(target -> target.isAlive() && (this == target.getLastHurtMob() || ((target instanceof Player player) && !player.getAbilities().invulnerable))),
                new SetPlayerLookTarget<>(),
                new SetRandomLookTarget<>().lookTime(e -> e.getRandom().nextInt(60, 80))),
            new OneRandomBehaviour<>(
                new SetRandomWalkTarget<>()
                    .speedModifier(1),
                new Idle<>()
                    .runFor(e -> e.getRandom().nextInt(30, 60)).cooldownFor(e -> e.getRandom().nextInt(100, 200)))
        );
    }

    @Override
    public ActivityBuilder<RunesteelGolemEntity> getFightingBehaviourGroup(RunesteelGolemEntity entity) {
        return ActivityBuilder.<RunesteelGolemEntity>create(Activity.FIGHT).behaviours(
            new InvalidateAttackTarget<>(),
            new FirstApplicableBehaviour<RunesteelGolemEntity>(
                new CastSpellBehaviour<RunesteelGolemEntity>(100, 18) {
                    @Override
                    protected boolean checkExtraStartConditions(ServerLevel level, RunesteelGolemEntity entity) {
                        if (!super.checkExtraStartConditions(level, entity)) return false;
                        return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                            .map(t -> entity.distanceTo(t) > 5.0f).orElse(false);
                    }
                    @Override
                    protected SpellCastContext buildContext(ServerLevel level, RunesteelGolemEntity entity, LivingEntity target) {
                        Vec3 aimStart = entity.position().add(0, 3.0, 0);
                        Vec3 aimDir = target.getEyePosition().subtract(aimStart).normalize();
                        return new SpellCastContext(level, entity, ItemStack.EMPTY, aimStart, aimDir,
                            entity.getXRot(), entity.getYRot(), target);
                    }
                    @Override
                    protected void onWindupStart(ServerLevel level, RunesteelGolemEntity entity, LivingEntity target) {
                        entity.triggerAnim("cast_controller", "cast");
                    }
                    @Override
                    protected void performCast(ServerLevel level, RunesteelGolemEntity entity, LivingEntity target, SpellCastContext ctx) {
                        SpellParams params = new SpellParams(10f, 1.75f,0.5f, 1.0f, 0, 0, 1,
                            ModElements.ARCANE.get(), MagicDamageType.SPIRIT);
                        ModSpellTypes.MAGIC_BEAM.get().onCast(ctx, params);
                    }
                },
                new SetWalkTargetToAttackTarget<RunesteelGolemEntity>().speedModifier((e, t) -> 1.0f)
            ),
            new AnimatableMeleeAttack<>(24)
        );
    }

    @Override
    public int getCurrentSwingDuration() {
      return 30;
    }

  @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunesteelGolemEntity>("base_controller", 5, state -> {
            if (this.deathTime > 0) return state.setAndContinue(DIE);
            if (state.isMoving()) return state.setAndContinue(DefaultAnimations.WALK);
            return state.setAndContinue(DefaultAnimations.IDLE);
        }));
        controllers.add(new AnimationController<RunesteelGolemEntity>("attack_controller", 0, state -> {
            if (this.deathTime > 0) return PlayState.STOP;
            if (this.swinging)
                return state.setAndContinue(ATTACK);
            state.controller().reset();
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<RunesteelGolemEntity>("cast_controller", 2, state -> PlayState.STOP)
            .triggerableAnim("cast", BEAM_CAST));
        controllers.add(new AnimationController<RunesteelGolemEntity>("spawn_controller", 0, state -> PlayState.STOP)
            .triggerableAnim("power_up", POWER_UP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
