package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DungeonPressurePlateBlockEntity extends BlockEntity {

    private BlockState mimickedState = Blocks.STONE.defaultBlockState();

    public DungeonPressurePlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_PRESSURE_PLATE.get(), pos, state);
    }

    public BlockState getMimickedState() {
        return mimickedState;
    }

    public void setMimickedState(BlockState state) {
        this.mimickedState = state;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("MimickedBlock", BlockState.CODEC, mimickedState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.mimickedState = input.read("MimickedBlock", BlockState.CODEC)
                .orElse(Blocks.STONE.defaultBlockState());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
