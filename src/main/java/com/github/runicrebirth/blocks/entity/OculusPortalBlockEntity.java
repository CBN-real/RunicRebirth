package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

public class OculusPortalBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { INACTIVE, IDLE, ACTIVATING, DEACTIVATING }

    private static final RawAnimation ANIM_INACTIVE = RawAnimation.begin().thenLoop("inactive");
    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_ACTIVATING = RawAnimation.begin().thenPlay("initiate_activated").thenLoop("hold_activated");
    private static final RawAnimation ANIM_DEACTIVATING = RawAnimation.begin().thenPlay("end_activated").thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private AnimState animState = AnimState.INACTIVE;

    @Nullable
    private Identifier selectedDungeonId;
    private int selectedDifficulty;
    @Nullable
    private BlockPos controllerPos;
    @Nullable
    private java.util.UUID activeInstanceId = null;

    public OculusPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OCULUS_PORTAL.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<OculusPortalBlockEntity>("portal", 0, animState -> {
            return switch (this.animState) {
                case INACTIVE -> animState.setAndContinue(ANIM_INACTIVE);
                case IDLE -> animState.setAndContinue(ANIM_IDLE);
                case ACTIVATING -> animState.setAndContinue(ANIM_ACTIVATING);
                case DEACTIVATING -> animState.setAndContinue(ANIM_DEACTIVATING);
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public AnimState getAnimState() { return animState; }

    public void setAnimState(AnimState newState) {
        if (this.animState != newState) {
            this.animState = newState;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                if (newState == AnimState.ACTIVATING) {
                    level.playSound(null, worldPosition, ModSounds.OCULUS_PORTAL_OPEN.get(),
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
    }

    public boolean hasSelectedDungeon() {
        return selectedDungeonId != null;
    }

    @Nullable
    public Identifier getSelectedDungeonId() {
        return selectedDungeonId;
    }

    @Nullable
    public Identifier getSelectedTierId() {
        return selectedDungeonId;
    }

    @Nullable
    public java.util.UUID getActiveInstanceId() { return activeInstanceId; }

    public void setActiveInstanceId(java.util.UUID id) { this.activeInstanceId = id; setChanged(); }

    public void clearActiveInstanceId() { this.activeInstanceId = null; setChanged(); }

    public int getSelectedDifficulty() {
        return selectedDifficulty;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        setChanged();
    }

    public void setSelectedDungeon(Identifier dungeonId, int difficulty) {
        this.selectedDungeonId = dungeonId;
        this.selectedDifficulty = difficulty;
        setAnimState(AnimState.ACTIVATING);
    }

    public void clearSelectedDungeon() {
        this.selectedDungeonId = null;
        this.selectedDifficulty = 0;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("animState", animState.name());
        if (selectedDungeonId != null) {
            output.putString("selectedDungeon", selectedDungeonId.toString());
            output.putInt("selectedDifficulty", selectedDifficulty);
        }
        if (controllerPos != null) {
            output.putLong("controllerPos", controllerPos.asLong());
        }
        if (activeInstanceId != null) {
            output.putString("activeInstanceId", activeInstanceId.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        try {
            animState = AnimState.valueOf(input.getStringOr("animState", "INACTIVE"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.INACTIVE;
        }
        String dungeonStr = input.getStringOr("selectedDungeon", "");
        if (!dungeonStr.isEmpty()) {
            selectedDungeonId = Identifier.parse(dungeonStr);
            selectedDifficulty = input.getIntOr("selectedDifficulty", 0);
        } else {
            selectedDungeonId = null;
            selectedDifficulty = 0;
        }
        long ctrlLong = input.getLongOr("controllerPos", Long.MIN_VALUE);
        controllerPos = ctrlLong != Long.MIN_VALUE ? BlockPos.of(ctrlLong) : null;
        String instanceIdStr = input.getStringOr("activeInstanceId", "");
        if (!instanceIdStr.isEmpty()) {
            try {
                activeInstanceId = java.util.UUID.fromString(instanceIdStr);
            } catch (IllegalArgumentException e) {
                activeInstanceId = null;
            }
        } else {
            activeInstanceId = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
