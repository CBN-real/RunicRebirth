package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.AoeTrackerGeoModel;
import com.github.runicrebirth.entities.spells.AoeTrackerEntity;
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
public class AoeTrackerRenderer extends GeoEntityRenderer<AoeTrackerEntity> {

    public AoeTrackerRenderer(EntityRendererProvider.Context context) {
        super(context, new AoeTrackerGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(AoeTrackerEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    protected void applyRotations(AoeTrackerEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
    }
}
