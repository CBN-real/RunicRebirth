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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
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

public class SkeletalMageAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<SkeletalMageAcolyteEntity> {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.skeletal_mage_acolyte.idle");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("animation.skeletal_mage_acolyte.cast");

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
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SkeletalMageAcolyteEntity>> getSensors() {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends SkeletalMageAcolyteEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new LookAtTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends SkeletalMageAcolyteEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<SkeletalMageAcolyteEntity>(
                new TargetOrRetaliate<>(), new SetPlayerLookTarget<>()
            ),
            new SetRandomWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SkeletalMageAcolyteEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new CircleAroundTargetBehaviour<>(7.0),
            new CastSpellBehaviour<SkeletalMageAcolyteEntity>(60) {
                @Override
                protected void performCast(ServerLevel level, SkeletalMageAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
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
                    SpellParams params = new SpellParams(5f, 1.0f,0.25f, 0.75f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.BLUNT);
                    ModSpellTypes.MAGIC_PROJECTILE.get().onCast(ctx, params);
                }
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "mage_ctrl", 5, state ->
            this.isAggressive() ? state.setAndContinue(CAST) : state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
