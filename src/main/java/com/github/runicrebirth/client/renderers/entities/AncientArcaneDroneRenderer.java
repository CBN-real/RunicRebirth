package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModDataTickets;
import com.github.runicrebirth.client.renderers.models.AncientArcaneDroneGeoModel;
import com.github.runicrebirth.entities.mobs.AncientArcaneDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class AncientArcaneDroneRenderer<R extends LivingEntityRenderState & GeoRenderState>
    extends GeoEntityRenderer<AncientArcaneDroneEntity, R> {

    public AncientArcaneDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientArcaneDroneGeoModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public void addRenderData(AncientArcaneDroneEntity entity, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(entity, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(ModDataTickets.HEAD_PITCH_RAD,
            Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot()) * Mth.DEG_TO_RAD);
        renderState.addGeckolibData(ModDataTickets.HEAD_YAW_RAD,
            Mth.lerp(partialTick,
                Mth.wrapDegrees(-entity.yHeadRotO + entity.yBodyRotO),
                Mth.wrapDegrees(-entity.yHeadRot + entity.yBodyRot)) * Mth.DEG_TO_RAD);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        snapshots.ifPresent("arcane_drone", body -> {
            body.setRotX(renderPassInfo.getGeckolibData(ModDataTickets.HEAD_PITCH_RAD));
            body.setRotY(renderPassInfo.getGeckolibData(ModDataTickets.HEAD_YAW_RAD));
        });
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yaw = renderPassInfo.renderState().getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
    }
}
