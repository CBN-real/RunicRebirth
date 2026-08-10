package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class MagicBeamEntity extends AbstractInstantSpellEntity {

    private static final RawAnimation INITIATE_SPELL = RawAnimation.begin().thenPlayAndHold("initiate_spell");
    private static final RawAnimation CODEX_ANIM = RawAnimation.begin().thenLoop("initiate_spell");

    private static final EntityDataAccessor<Float> DATA_DISTANCE =
        SynchedEntityData.defineId(MagicBeamEntity.class, EntityDataSerializers.FLOAT);

    private static final int MAX_LIFETIME = 20;

    public MagicBeamEntity(EntityType<? extends MagicBeamEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 0;
        this.endTicks = 0;
    }

    public MagicBeamEntity(Level level, LivingEntity owner, Vec3 start, float distance,
                           Vec3 aimDir, SpellParams params) {
        this(ModEntities.MAGIC_BEAM.get(), level);
        this.entityData.set(DATA_DISTANCE, distance);
        this.setPos(start.x, start.y, start.z);

        double hDist = aimDir.horizontalDistance();
        this.setYRot((float)(Mth.atan2(aimDir.x, aimDir.z) * (180.0 / Math.PI)));
        this.setXRot((float)(-(Mth.atan2(aimDir.y, hDist)) * (180.0 / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        initFromParams(params);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DISTANCE, 0f);
    }

    public float getBeamDistance() {
        return this.entityData.get(DATA_DISTANCE);
    }

    @Override
    protected void onChargingTick() {
        if (phaseAge == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.SPELLS_LASER_SHOT.get(), SoundSource.PLAYERS, 0.4f, 1.3f);
        }
    }

    @Override
    protected void onActiveTick() {
        if (age >= MAX_LIFETIME) {
            discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "beam", 0, state -> {
          if (!this.isAddedToLevel()) {
            double currentTick = this.getTick(this);
            var controller = state.getController();
            var animState = controller.getAnimationState();
            state.setAnimation(CODEX_ANIM);
            return PlayState.CONTINUE;
          }

            state.setAnimation(INITIATE_SPELL);
            return PlayState.CONTINUE;
        }));
    }
}
