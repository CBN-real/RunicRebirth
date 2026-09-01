package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.AoeTrackerGeoModel;
import com.github.runicrebirth.entities.spells.AoeTrackerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class AoeTrackerRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<AoeTrackerEntity, R> {

    public AoeTrackerRenderer(EntityRendererProvider.Context context) {
        super(context, new AoeTrackerGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
    }
}
