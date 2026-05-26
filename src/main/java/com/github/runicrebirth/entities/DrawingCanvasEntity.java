package com.github.runicrebirth.entities;

import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModEntities;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DrawingCanvasEntity extends Entity implements GeoEntity {

    private static final RawAnimation INITIATE_AND_HOLD = RawAnimation.begin().thenPlay("initiate_canvas").thenLoop("hold_canvas");
    private static final RawAnimation END_CANVAS = RawAnimation.begin().thenPlayAndHold("end_canvas");

    private static final EntityDataAccessor<String> DATA_OWNER_UUID =
        SynchedEntityData.defineId(DrawingCanvasEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
        SynchedEntityData.defineId(DrawingCanvasEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_SELECTED_ELEMENT =
        SynchedEntityData.defineId(DrawingCanvasEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_SELECTED_TIER =
        SynchedEntityData.defineId(DrawingCanvasEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
        SynchedEntityData.defineId(DrawingCanvasEntity.class, EntityDataSerializers.INT);

    private static final float DISTANCE_FROM_EYE = 1.0f;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int ENDING_DURATION = 30;

    private int age;
    private int endingTicks = -1;
    private boolean tierEverSelected;

    public DrawingCanvasEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static DrawingCanvasEntity spawnFor(ServerPlayer player) {
        DrawingCanvasEntity entity = new DrawingCanvasEntity(ModEntities.DRAWING_CANVAS.get(), player.level());
        entity.entityData.set(DATA_OWNER_UUID, player.getUUID().toString());
        entity.entityData.set(DATA_OWNER_ID, player.getId());
        entity.snapToOwner(player);
        player.level().addFreshEntity(entity);
        return entity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNER_UUID, "");
        builder.define(DATA_OWNER_ID, -1);
        builder.define(DATA_SELECTED_ELEMENT, "runicrebirth:arcane");
        builder.define(DATA_SELECTED_TIER, -1);
        builder.define(DATA_PHASE, 0);
    }

    public void setSelectedElement(String elementId) {
        this.entityData.set(DATA_SELECTED_ELEMENT, elementId);
    }

    public void setSelectedTier(int tier) {
        this.entityData.set(DATA_SELECTED_TIER, tier);
    }

    public String getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID);
    }

    public String getSelectedElement() {
        return this.entityData.get(DATA_SELECTED_ELEMENT);
    }

    public int getSelectedTier() {
        return this.entityData.get(DATA_SELECTED_TIER);
    }

    public int getPhaseOrdinal() {
        return this.entityData.get(DATA_PHASE);
    }

    public void beginEnding() {
        if (endingTicks < 0) {
            this.entityData.set(DATA_PHASE, 2);
            endingTicks = 0;
        }
    }

    @Override
    public void tick() {
        if (getPhaseOrdinal() == 2) {
            tickCount++;
            if (!level().isClientSide) {
                endingTicks++;
                if (endingTicks >= ENDING_DURATION) {
                    discard();
                }
            }
            return;
        }

        super.tick();
        if (!level().isClientSide) {
            age++;
            String ownerUuid = getOwnerUUID();
            if (ownerUuid.isEmpty()) { discard(); return; }
            ServerPlayer owner = level().getServer().getPlayerList().getPlayer(UUID.fromString(ownerUuid));
            if (owner == null) { discard(); return; }

            if (age > 6000) { discard(); return; }
            if (!MagicData.of(owner).isDrawing()) {
                beginEnding();
                return;
            }
            if (age > 20 && getPhaseOrdinal() == 0) {
                this.entityData.set(DATA_PHASE, 1);
            }
            snapToOwner(owner);
        } else {
            int ownerId = this.entityData.get(DATA_OWNER_ID);
            if (ownerId != -1) {
                Entity owner = this.level().getEntity(ownerId);
                if (owner instanceof net.minecraft.world.entity.player.Player player) {
                    snapToOwner(player);
                }
            }
        }
    }

    @Override
    public void push(double x, double y, double z) {
    }

    private void snapToOwner(Entity owner) {
        Vec3 eye;
        Vec3 look;
        if (owner instanceof net.minecraft.world.entity.player.Player player) {
            eye = player.getEyePosition();
            look = player.getLookAngle();
        } else {
            eye = owner.getEyePosition();
            look = owner.getForward();
        }
        double x = eye.x + look.x * DISTANCE_FROM_EYE;
        double y = eye.y + look.y * DISTANCE_FROM_EYE;
        double z = eye.z + look.z * DISTANCE_FROM_EYE;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.setPos(x, y, z);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.setYRot(owner.getYRot());
        this.setXRot(owner.getXRot());
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "canvas_phase", 0, state -> {
            if (state.getAnimatable().getPhaseOrdinal() == 2) {
                state.setAnimation(END_CANVAS);
            } else {
                state.setAnimation(INITIATE_AND_HOLD);
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "element_selection", 0, state -> {
            if (state.getAnimatable().getPhaseOrdinal() != 1) return PlayState.STOP;
            String elementId = state.getAnimatable().getSelectedElement();
            String elementName = elementId.contains(":") ? elementId.substring(elementId.indexOf(':') + 1) : elementId;
            state.setAnimation(RawAnimation.begin().thenLoop(elementName + "_selected"));
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "tier_selection", 0, state -> {
            if (state.getAnimatable().getPhaseOrdinal() != 1) return PlayState.STOP;
            DrawingCanvasEntity entity = state.getAnimatable();
            int tier = entity.getSelectedTier();
            if (tier >= 0) {
                entity.tierEverSelected = true;
                String selectedAnimName = switch (tier) {
                    case 0 -> "basic_selected";
                    case 1 -> "intermediate_selected";
                    case 2 -> "advanced_selected";
                    default -> "none_selected";
                };
                String holdAnimName = switch (tier) {
                    case 0 -> "basic_hold";
                    case 1 -> "intermediate_hold";
                    case 2 -> "advanced_hold";
                    default -> "hold_canvas";
                };
                state.setAnimation(RawAnimation.begin().thenPlay(selectedAnimName).thenLoop(holdAnimName));
            } else if (entity.tierEverSelected) {
                state.setAnimation(RawAnimation.begin().thenPlay("none_selected").thenLoop("hold_canvas"));
            } else {
                return PlayState.STOP;
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
