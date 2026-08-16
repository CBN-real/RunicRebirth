package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.SkeletalMageAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletalMageAcolyteRenderer extends GeoEntityRenderer<SkeletalMageAcolyteEntity> {

    public SkeletalMageAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletalMageAcolyteGeoModel());
    }

    @Override
    public @Nullable RenderType getRenderType(SkeletalMageAcolyteEntity animatable,
        ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
