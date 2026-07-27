package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.TargetCircleGeoModel;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class TargetCircleRenderer extends GeoEntityRenderer<TargetCircleEntity> {

    public TargetCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetCircleGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(TargetCircleEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    protected void applyRotations(TargetCircleEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        // Geo root bone handles horizontal orientation; no entity rotation applied
    }
}
