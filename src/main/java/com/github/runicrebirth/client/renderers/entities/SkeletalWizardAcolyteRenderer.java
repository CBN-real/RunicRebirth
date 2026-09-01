package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModDataTickets;
import com.github.runicrebirth.client.renderers.models.SkeletalWizardAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class SkeletalWizardAcolyteRenderer<R extends LivingEntityRenderState & GeoRenderState>
    extends GeoEntityRenderer<SkeletalWizardAcolyteEntity, R> {

    public SkeletalWizardAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletalWizardAcolyteGeoModel());
    }

    @Override
    public void addRenderData(SkeletalWizardAcolyteEntity entity, Void relatedObject, R renderState, float partialTick) {
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
        snapshots.ifPresent("bipedHead", head -> {
            head.setRotX(renderPassInfo.getGeckolibData(ModDataTickets.HEAD_PITCH_RAD));
            head.setRotY(renderPassInfo.getGeckolibData(ModDataTickets.HEAD_YAW_RAD));
        });
    }
}
