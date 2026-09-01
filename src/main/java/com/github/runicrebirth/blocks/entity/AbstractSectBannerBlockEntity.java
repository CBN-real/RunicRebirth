package com.github.runicrebirth.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public abstract class AbstractSectBannerBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected BannerPatternLayers patterns = BannerPatternLayers.EMPTY;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected AbstractSectBannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public @Nullable DyeColor getBaseColor() { return null; }

    public BannerPatternLayers getPatterns() { return patterns; }

    public void setPatterns(BannerPatternLayers p) {
        this.patterns = p != null ? p : BannerPatternLayers.EMPTY;
        setChanged();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!patterns.layers().isEmpty()) {
            output.store("patterns", BannerPatternLayers.CODEC, patterns);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.patterns = input.read("patterns", BannerPatternLayers.CODEC).orElse(BannerPatternLayers.EMPTY);
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
