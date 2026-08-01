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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
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

public class SkeletalWizardAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<SkeletalWizardAcolyteEntity> {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.skeletal_wizard_acolyte.idle");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("animation.skeletal_wizard_acolyte.cast");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SkeletalWizardAcolyteEntity(EntityType<? extends SkeletalWizardAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
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
    public List<? extends ExtendedSensor<? extends SkeletalWizardAcolyteEntity>> getSensors() {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends SkeletalWizardAcolyteEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new LookAtTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends SkeletalWizardAcolyteEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<SkeletalWizardAcolyteEntity>(
                new TargetOrRetaliate<>(), new SetPlayerLookTarget<>()
            )
        );
    }

    @Override
    public BrainActivityGroup<? extends SkeletalWizardAcolyteEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
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
                    Vec3 spawnPos = entity.position().add(entity.getLookAngle().scale(1.0));
                    MagicBlastEntity blast = new MagicBlastEntity(ModEntities.MAGIC_BLAST.get(), level);
                    SpellParams params = new SpellParams(2.5f, 2.0f, 1.0f, 0, 0, 0,
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
                    SpellParams params = new SpellParams(6f, 1.5f, 1.5f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.SHARP);
                    ModSpellTypes.MAGIC_ARROW.get().onCast(ctx, params);
                }
            }
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "wizard_ctrl", 5, state ->
            this.isAggressive() ? state.setAndContinue(CAST) : state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
