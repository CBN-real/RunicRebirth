package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class TatteredSectBannerBlockEntity extends AbstractSectBannerBlockEntity {

    private DyeColor baseColor = DyeColor.WHITE;

    public TatteredSectBannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TATTERED_SECT_BANNER.get(), pos, state);
    }

    @Override
    public DyeColor getBaseColor() { return baseColor; }

    public void setBaseColor(DyeColor color) {
        this.baseColor = color != null ? color : DyeColor.WHITE;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("base_color", baseColor.getId());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("base_color")) {
            baseColor = DyeColor.byId(tag.getInt("base_color"));
        }
    }
}
