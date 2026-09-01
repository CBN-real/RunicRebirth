package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicHammerGeoModel;
import com.github.runicrebirth.entities.spells.MagicHammerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicHammerRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicHammerEntity, R> {

    private static final DataTicket<Float> HAMMER_PITCH = DataTicket.create("runicrebirth:hammer_pitch", Float.class);

    public MagicHammerRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicHammerGeoModel());
    }

    @Override
    public void extractRenderState(MagicHammerEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 0.001) {
            float xRot = (float) (Mth.atan2(motion.y, motion.horizontalDistance()) * Mth.RAD_TO_DEG);
            renderState.addGeckolibData(HAMMER_PITCH, xRot);
        } else {
            renderState.addGeckolibData(HAMMER_PITCH, 0f);
        }
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        super.adjustRenderPose(renderPassInfo);
        float pitch = renderPassInfo.getOrDefaultGeckolibData(HAMMER_PITCH, 0f);
        if (Math.abs(pitch) > 0.001f) {
            renderPassInfo.poseStack().mulPose(Axis.XP.rotationDegrees(pitch));
        }
    }
}
