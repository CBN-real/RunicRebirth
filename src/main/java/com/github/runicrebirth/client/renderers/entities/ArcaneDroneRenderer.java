package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.ArcaneDroneGeoModel;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class ArcaneDroneRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ArcaneDroneEntity, R> {

    public ArcaneDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneDroneGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public boolean shouldRender(ArcaneDroneEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - renderPassInfo.renderState().yRot));
    }
}
