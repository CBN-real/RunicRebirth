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

public class ZombifiedArtificerAcolyteEntity extends Monster implements GeoEntity, SmartBrainOwner<ZombifiedArtificerAcolyteEntity> {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.zombified_artificer_acolyte.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.zombified_artificer_acolyte.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.zombified_artificer_acolyte.attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ZombifiedArtificerAcolyteEntity(EntityType<? extends ZombifiedArtificerAcolyteEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.ATTACK_DAMAGE, 2.5)
            .add(Attributes.FOLLOW_RANGE, 20.0);
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
    public List<? extends ExtendedSensor<? extends ZombifiedArtificerAcolyteEntity>> getSensors() {
        return ObjectArrayList.of(new NearbyLivingEntitySensor<>(), new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedArtificerAcolyteEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new LookAtTarget<>(), new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedArtificerAcolyteEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<ZombifiedArtificerAcolyteEntity>(
                new TargetOrRetaliate<>(), new SetPlayerLookTarget<>()
            ),
            new SetRandomWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends ZombifiedArtificerAcolyteEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new SetWalkTargetToAttackTarget<>(),
            new CastSpellBehaviour<ZombifiedArtificerAcolyteEntity>(40) {
                @Override
                protected void performCast(ServerLevel level, ZombifiedArtificerAcolyteEntity entity, LivingEntity target, SpellCastContext ctx) {
                    SpellParams params = new SpellParams(5f, 1.0f, 0.75f, 0, 0, 0,
                        ModElements.ARCANE.get(), MagicDamageType.BLUNT);
                    params.damage = params.damage * (1.0f + params.size) / 2.0f;
                    ModSpellTypes.MAGIC_PROJECTILE.get().onCast(ctx, params);
                }
            },
            new AnimatableMeleeAttack<>(20)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "artificer_ctrl", 5, state -> {
            if (this.swinging) return state.setAndContinue(ATTACK);
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001) return state.setAndContinue(WALK);
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
