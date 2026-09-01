package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.TargetCircleGeoModel;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class TargetCircleRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<TargetCircleEntity, R> {

    public TargetCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetCircleGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    protected void applyRotations(com.geckolib.renderer.base.RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        // Geo root bone handles horizontal orientation; no entity rotation applied
    }
}
