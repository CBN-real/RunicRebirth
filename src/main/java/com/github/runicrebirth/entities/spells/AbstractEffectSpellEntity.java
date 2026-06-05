package com.github.runicrebirth.entities.spells;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.UUID;

public abstract class AbstractEffectSpellEntity extends AbstractInstantSpellEntity {

    private static final RawAnimation SPELLBOOK_INTRO = RawAnimation.begin().thenPlay("initiate_spell");
    private static final RawAnimation SPELLBOOK_HOLD = RawAnimation.begin().thenLoop("hold_spell");
    private static final RawAnimation SPELLBOOK_END = RawAnimation.begin().thenPlay("end_spell");
    private static final int SPELLBOOK_HOLD_TICKS = 60;

    private static final EntityDataAccessor<Integer> DATA_FOLLOWED_ID =
        SynchedEntityData.defineId(AbstractEffectSpellEntity.class, EntityDataSerializers.INT);

    protected UUID followedUUID;
    protected int maxDuration = 600;
    private int displayPhase;
    private double holdStartTick;
    private double phaseStartTick;
    private boolean phaseAnimSet;

    protected AbstractEffectSpellEntity(EntityType<? extends AbstractEffectSpellEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FOLLOWED_ID, -1);
    }

    protected void setFollowedEntity(Entity entity) {
        this.followedUUID = entity.getUUID();
        this.entityData.set(DATA_FOLLOWED_ID, entity.getId());
    }

    public int getFollowedEntityId() {
        return this.entityData.get(DATA_FOLLOWED_ID);
    }

    protected void snapToFollowed(Entity target) {
        this.setPos(target.getX(), target.getY(), target.getZ());
        this.xo = target.xo;
        this.yo = target.yo;
        this.zo = target.zo;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spell_phase", 0, state -> {
            if (!this.isAddedToLevel()) {
                double currentTick = this.getTick(this);
                var controller = state.getController();
                switch (displayPhase) {
                    case 0:
                        if (!phaseAnimSet) {
                            state.setAnimation(SPELLBOOK_INTRO);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > 0.5 && controller.getAnimationState() == AnimationController.State.STOPPED) {
                            displayPhase = 1;
                            holdStartTick = currentTick;
                            phaseAnimSet = false;
                        }
                        break;
                    case 1:
                        if (!phaseAnimSet) {
                            state.setAnimation(HOLD_SPELL);
                            phaseAnimSet = true;
                        }
                        if (currentTick - holdStartTick >= SPELLBOOK_HOLD_TICKS) {
                            displayPhase = 2;
                            phaseAnimSet = false;
                        }
                        break;
                    case 2:
                        if (!phaseAnimSet) {
                            state.setAnimation(SPELLBOOK_END);
                            phaseAnimSet = true;
                            phaseStartTick = currentTick;
                        }
                        if (currentTick - phaseStartTick > 0.5 && controller.getAnimationState() == AnimationController.State.STOPPED) {
                            displayPhase = 0;
                            phaseAnimSet = false;
                            controller.forceAnimationReset();

                        }
                        break;
                }
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().getPhase() == SpellPhase.ENDING) {
                state.setAnimation(END_SPELL);
            } else {
                state.setAnimation(INITIATE_AND_HOLD);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }
}
