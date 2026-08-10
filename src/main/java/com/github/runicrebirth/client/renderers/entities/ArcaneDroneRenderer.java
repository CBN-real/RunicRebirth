package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.ArcaneDroneGeoModel;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ArcaneDroneRenderer extends GeoEntityRenderer<ArcaneDroneEntity> {

    public ArcaneDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneDroneGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(ArcaneDroneEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public boolean shouldRender(ArcaneDroneEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    protected void applyRotations(ArcaneDroneEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
    }
}
