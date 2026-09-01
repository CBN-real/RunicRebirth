package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicMeteorCircleGeoModel;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicMeteorCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicMeteorCircleEntity, R> {

    private static final DataTicket<Float> METEOR_CIRCLE_SCALE = DataTicket.create("runicrebirth:meteor_circle_scale", Float.class);

    public MagicMeteorCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicMeteorCircleGeoModel());
    }

    @Override
    public void extractRenderState(MagicMeteorCircleEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(METEOR_CIRCLE_SCALE, entity.getCircleScale());
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yRot = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);
        float xRot = renderPassInfo.getOrDefaultGeckolibData(AbstractSpellRenderer.SPELL_XROT, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-38f + xRot));
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(METEOR_CIRCLE_SCALE, 1f);
        renderPassInfo.poseStack().scale(scale, scale, scale);
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }
}
