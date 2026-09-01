package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicBallistaCircleGeoModel;
import com.github.runicrebirth.entities.spells.MagicBallistaCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicBallistaCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicBallistaCircleEntity, R> {

    private static final DataTicket<Float> CIRCLE_YROT = DataTicket.create("runicrebirth:ballista_yrot", Float.class);
    private static final DataTicket<Float> CIRCLE_XROT = DataTicket.create("runicrebirth:ballista_xrot", Float.class);
    private static final DataTicket<Float> CIRCLE_SCALE = DataTicket.create("runicrebirth:ballista_scale", Float.class);

    public MagicBallistaCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBallistaCircleGeoModel());
    }

    @Override
    public void extractRenderState(MagicBallistaCircleEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(CIRCLE_YROT, entity.getYRot());
        renderState.addGeckolibData(CIRCLE_XROT, entity.getXRot());
        renderState.addGeckolibData(CIRCLE_SCALE, entity.getCircleScale());
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yRot = renderPassInfo.getOrDefaultGeckolibData(CIRCLE_YROT, 0f);
        float xRot = renderPassInfo.getOrDefaultGeckolibData(CIRCLE_XROT, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(CIRCLE_SCALE, 1f);
        renderPassInfo.poseStack().scale(scale, scale, scale);
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }
}
