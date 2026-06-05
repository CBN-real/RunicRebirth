package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.dungeon.DungeonType;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

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
    private ResourceLocation selectedDungeonId;
    private int selectedDifficulty;
    @Nullable
    private BlockPos controllerPos;

    public OculusPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OCULUS_PORTAL.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "portal", 0, animState -> {
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
    public DungeonType getSelectedDungeonType() {
        return selectedDungeonId != null ? DungeonType.byId(selectedDungeonId) : null;
    }

    @Nullable
    public ResourceLocation getSelectedDungeonId() {
        return selectedDungeonId;
    }

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

    public void setSelectedDungeon(ResourceLocation dungeonId, int difficulty) {
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("animState", animState.name());
        if (selectedDungeonId != null) {
            tag.putString("selectedDungeon", selectedDungeonId.toString());
            tag.putInt("selectedDifficulty", selectedDifficulty);
        }
        if (controllerPos != null) {
            tag.putLong("controllerPos", controllerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            animState = AnimState.valueOf(tag.getString("animState"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.INACTIVE;
        }
        if (tag.contains("selectedDungeon")) {
            selectedDungeonId = ResourceLocation.parse(tag.getString("selectedDungeon"));
            selectedDifficulty = tag.getInt("selectedDifficulty");
        } else {
            selectedDungeonId = null;
            selectedDifficulty = 0;
        }
        if (tag.contains("controllerPos")) {
            controllerPos = BlockPos.of(tag.getLong("controllerPos"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
