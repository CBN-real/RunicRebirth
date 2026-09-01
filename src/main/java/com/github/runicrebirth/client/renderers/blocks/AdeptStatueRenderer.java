package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AdeptStatueBlock;
import com.github.runicrebirth.blocks.entity.AdeptStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class AdeptStatueRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<AdeptStatueBlockEntity, R> {

    public AdeptStatueRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AdeptStatueModel());
    }
}
