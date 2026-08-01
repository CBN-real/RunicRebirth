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
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RunesteelGolemEntity extends Monster implements GeoEntity, SmartBrainOwner<RunesteelGolemEntity> {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.runesteel_golem.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.runesteel_golem.attack");
    private static final RawAnimation BEAM_CAST = RawAnimation.begin().thenPlay("animation.runesteel_golem.beam_cast");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunesteelGolemEntity(EntityType<? extends RunesteelGolemEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0)
            .add(Attributes.MOVEMENT_SPEED, 0.18)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
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
    public List<? extends ExtendedSensor<? extends RunesteelGolemEntity>> getSensors() {
        return ObjectArrayList.of(
            new NearbyLivingEntitySensor<>(),
            new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends RunesteelGolemEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
            new LookAtTarget<>(),
            new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends RunesteelGolemEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<RunesteelGolemEntity>(
                new TargetOrRetaliate<>(),
                new SetPlayerLookTarget<>()
            ),
            new SetRandomWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends RunesteelGolemEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new FirstApplicableBehaviour<RunesteelGolemEntity>(
                new CastSpellBehaviour<RunesteelGolemEntity>(60) {
                    @Override
                    protected boolean checkExtraStartConditions(ServerLevel level, RunesteelGolemEntity entity) {
                        if (!super.checkExtraStartConditions(level, entity)) return false;
                        return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                            .map(t -> entity.distanceTo(t) > 5.0f).orElse(false);
                    }
                    @Override
                    protected void performCast(ServerLevel level, RunesteelGolemEntity entity, LivingEntity target, SpellCastContext ctx) {
                        SpellParams params = new SpellParams(3f, 1.0f, 1.0f, 0, 0, 0,
                            ModElements.ARCANE.get(), MagicDamageType.SPIRIT);
                        ModSpellTypes.MAGIC_BEAM.get().onCast(ctx, params);
                    }
                },
                new SetWalkTargetToAttackTarget<RunesteelGolemEntity>().speedMod((e, t) -> 0.5f)
            ),
            new AnimatableMeleeAttack<>(10)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "golem_ctrl", 5, state -> {
            if (this.swinging) return state.setAndContinue(ATTACK);
            if (this.isAggressive()) return state.setAndContinue(BEAM_CAST);
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
