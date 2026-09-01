package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonBoulderSpawnerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class DungeonBoulderSpawnerRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<DungeonBoulderSpawnerBlockEntity, R> {

    public DungeonBoulderSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonBoulderSpawnerModel());
    }
}
