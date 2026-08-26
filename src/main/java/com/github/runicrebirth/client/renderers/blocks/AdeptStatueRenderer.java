package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AdeptStatueBlock;
import com.github.runicrebirth.blocks.entity.AdeptStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class AdeptStatueRenderer extends AbstractRunicBlockRenderer<AdeptStatueBlockEntity> {

    public AdeptStatueRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AdeptStatueModel());
    }
}
