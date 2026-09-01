package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.HammerDroneGeoModel;
import com.github.runicrebirth.entities.HammerDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class HammerDroneRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<HammerDroneEntity, R> {

    public HammerDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new HammerDroneGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public boolean shouldRender(HammerDroneEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    protected void applyRotations(com.geckolib.renderer.base.RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yaw = renderPassInfo.getOrDefaultGeckolibData(com.geckolib.constant.DataTickets.ENTITY_BODY_YAW, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
    }
}
