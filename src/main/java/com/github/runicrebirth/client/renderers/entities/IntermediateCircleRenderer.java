package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.IntermediateCircleGeoModel;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class IntermediateCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractCircleRenderer<IntermediateCircleEntity, R> {

    public IntermediateCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new IntermediateCircleGeoModel());
    }
}
