package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class SectBannerBlockEntity extends AbstractSectBannerBlockEntity {

    private DyeColor baseColor = DyeColor.WHITE;

    public SectBannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SECT_BANNER.get(), pos, state);
    }

    @Override
    public DyeColor getBaseColor() { return baseColor; }

    public void setBaseColor(DyeColor color) {
        this.baseColor = color != null ? color : DyeColor.WHITE;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("base_color", baseColor.getId());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        baseColor = DyeColor.byId(input.getIntOr("base_color", DyeColor.WHITE.getId()));
    }
}
