package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicSlashGeoModel;
import com.github.runicrebirth.entities.spells.MagicSlashEntity;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicSlashRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicSlashEntity, R> {

    private static final com.geckolib.constant.dataticket.DataTicket<Float> SLASH_YROT =
        com.geckolib.constant.dataticket.DataTicket.create("runicrebirth:slash_yrot", Float.class);

    public MagicSlashRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicSlashGeoModel());
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

    @Override
    public void extractRenderState(MagicSlashEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        net.minecraft.world.phys.Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 0.001) {
            float yRot = (float)(net.minecraft.util.Mth.atan2(motion.x, motion.z) * net.minecraft.util.Mth.RAD_TO_DEG);
            renderState.addGeckolibData(SLASH_YROT, yRot);
        } else {
            renderState.addGeckolibData(SLASH_YROT, 0f);
        }
    }

    @Override
    protected void applyRotations(com.geckolib.renderer.base.RenderPassInfo<R> renderPassInfo, com.mojang.blaze3d.vertex.PoseStack poseStack, float nativeScale) {
        float yaw = renderPassInfo.getOrDefaultGeckolibData(SLASH_YROT, 0f);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yaw));
    }
}
