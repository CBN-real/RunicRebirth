package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!patterns.layers().isEmpty()) {
            BannerPatternLayers.CODEC
                    .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), patterns)
                    .resultOrPartial(e -> RunicRebirth.LOGGER.error("Banner pattern encode error: {}", e))
                    .ifPresent(nbt -> tag.put("patterns", nbt));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("patterns")) {
            BannerPatternLayers.CODEC
                    .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("patterns"))
                    .resultOrPartial(e -> RunicRebirth.LOGGER.error("Banner pattern decode error: {}", e))
                    .ifPresent(p -> this.patterns = p);
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
