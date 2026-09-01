package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicBeamGeoModel;
import com.github.runicrebirth.entities.spells.MagicBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicBeamRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicBeamEntity, R> {

    private static final DataTicket<Float> BEAM_YROT     = DataTicket.create("runicrebirth:beam_yrot", Float.class);
    private static final DataTicket<Float> BEAM_XROT     = DataTicket.create("runicrebirth:beam_xrot", Float.class);
    private static final DataTicket<Float> BEAM_DISTANCE = DataTicket.create("runicrebirth:beam_distance", Float.class);
    private static final DataTicket<Float> BEAM_SIZE     = DataTicket.create("runicrebirth:beam_size", Float.class);
    private static final DataTicket<Boolean> BEAM_ACTIVE = DataTicket.create("runicrebirth:beam_active", Boolean.class);

    public MagicBeamRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBeamGeoModel());
    }

    @Override
    public boolean shouldRender(MagicBeamEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void extractRenderState(MagicBeamEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(BEAM_YROT, entity.getYRot());
        renderState.addGeckolibData(BEAM_XROT, entity.getXRot());
        renderState.addGeckolibData(BEAM_DISTANCE, entity.getBeamDistance());
        renderState.addGeckolibData(BEAM_SIZE, entity.getProjectileSize());
        renderState.addGeckolibData(BEAM_ACTIVE, entity.isAddedToLevel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        Boolean active = renderPassInfo.getOrDefaultGeckolibData(BEAM_ACTIVE, false);
        if (Boolean.TRUE.equals(active)) {
            float yaw = renderPassInfo.getOrDefaultGeckolibData(BEAM_YROT, 0f);
            float pitch = renderPassInfo.getOrDefaultGeckolibData(BEAM_XROT, 0f);
            renderPassInfo.poseStack().mulPose(Axis.YP.rotationDegrees(yaw));
            renderPassInfo.poseStack().mulPose(Axis.XP.rotationDegrees(180 + pitch));
        }
    }

    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        Boolean active = renderState.getOrDefaultGeckolibData(BEAM_ACTIVE, false);
        if (!Boolean.TRUE.equals(active)) {
            performRenderPass(renderState, poseStack, renderTasks, cameraState);
            return;
        }
        float distance = renderState.getOrDefaultGeckolibData(BEAM_DISTANCE, 0f);
        if (distance <= 0) return;
        float size = renderState.getOrDefaultGeckolibData(BEAM_SIZE, 1f);
        float scaledDistance = (size > 0) ? distance / size : distance;
        for (float d = (float) Math.floor(scaledDistance - 1.0f); d >= 0; d -= 1.0f) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -d);
            performRenderPass(renderState, poseStack, renderTasks, cameraState);
            poseStack.popPose();
        }
    }
}
