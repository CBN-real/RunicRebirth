package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ThrownRunicDaggerGeoModel;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class ThrownRunicDaggerRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ThrownRunicDaggerEntity, R> {

    private static final com.geckolib.constant.dataticket.DataTicket<ThrownRunicDaggerEntity.Phase> DAGGER_PHASE =
        com.geckolib.constant.dataticket.DataTicket.create("runicrebirth:dagger_phase", ThrownRunicDaggerEntity.Phase.class);

    public ThrownRunicDaggerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ThrownRunicDaggerGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public boolean shouldRender(ThrownRunicDaggerEntity entity,
                                 net.minecraft.client.renderer.culling.Frustum frustum,
                                 double x, double y, double z) {
        return true;
    }

    @Override
    public void extractRenderState(ThrownRunicDaggerEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(DAGGER_PHASE, entity.getPhase());
    }

    @Override
    protected void applyRotations(com.geckolib.renderer.base.RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yaw = renderPassInfo.getOrDefaultGeckolibData(com.geckolib.constant.DataTickets.ENTITY_BODY_YAW, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
        ThrownRunicDaggerEntity.Phase phase = renderPassInfo.getOrDefaultGeckolibData(DAGGER_PHASE, ThrownRunicDaggerEntity.Phase.SPINNING);
        if (phase == ThrownRunicDaggerEntity.Phase.STUCK_BLOCK || phase == ThrownRunicDaggerEntity.Phase.STUCK_ENTITY) {
            float pitch = renderPassInfo.getOrDefaultGeckolibData(com.geckolib.constant.DataTickets.ENTITY_PITCH, 0f);
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90f));
        }
    }
}
