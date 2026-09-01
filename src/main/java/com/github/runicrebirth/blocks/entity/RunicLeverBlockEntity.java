package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class RunicLeverBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { OFF, ON }

    private static final RawAnimation ANIM_OFF = RawAnimation.begin().thenPlayAndHold("close");
    private static final RawAnimation ANIM_ON  = RawAnimation.begin().thenPlayAndHold("open");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private AnimState animState = AnimState.OFF;

    public RunicLeverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNIC_LEVER.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunicLeverBlockEntity>("controller", 0, state ->
            switch (animState) {
                case OFF -> state.setAndContinue(ANIM_OFF);
                case ON  -> state.setAndContinue(ANIM_ON);
            }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setPowered(boolean powered) {
        AnimState next = powered ? AnimState.ON : AnimState.OFF;
        if (animState != next) {
            animState = next;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("animState", animState.name());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        try {
            animState = AnimState.valueOf(input.getStringOr("animState", "OFF"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.OFF;
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
