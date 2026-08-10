package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("MimickedBlock", NbtUtils.writeBlockState(mimickedState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("MimickedBlock")) {
            mimickedState = NbtUtils.readBlockState(registries.lookup(net.minecraft.core.registries.Registries.BLOCK).orElseThrow(), tag.getCompound("MimickedBlock"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("MimickedBlock", NbtUtils.writeBlockState(mimickedState));
        return tag;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}
