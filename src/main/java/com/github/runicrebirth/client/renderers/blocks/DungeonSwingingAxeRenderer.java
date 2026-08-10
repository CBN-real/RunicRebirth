package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonSwingingAxeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DungeonSwingingAxeRenderer extends AbstractRunicBlockRenderer<DungeonSwingingAxeBlockEntity> {

    public DungeonSwingingAxeRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonSwingingAxeModel());
    }
}
