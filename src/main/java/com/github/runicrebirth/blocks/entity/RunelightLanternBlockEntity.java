package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class RunelightLanternBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int selectedRune = -1;

    public RunelightLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNELIGHT_LANTERN.get(), pos, state);
    }

    public int getSelectedRune() {
        return selectedRune < 0 ? 0 : selectedRune;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (selectedRune == -1 && level != null) {
            selectedRune = level.getRandom().nextInt(8);
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("selectedRune", selectedRune);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        selectedRune = input.getIntOr("selectedRune", -1);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunelightLanternBlockEntity>("controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
