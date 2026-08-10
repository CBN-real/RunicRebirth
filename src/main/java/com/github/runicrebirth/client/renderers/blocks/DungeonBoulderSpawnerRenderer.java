package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonBoulderSpawnerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DungeonBoulderSpawnerRenderer extends AbstractRunicBlockRenderer<DungeonBoulderSpawnerBlockEntity> {

    public DungeonBoulderSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonBoulderSpawnerModel());
    }
}
