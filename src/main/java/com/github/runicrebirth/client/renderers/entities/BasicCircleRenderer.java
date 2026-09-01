package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.BasicCircleGeoModel;
import com.github.runicrebirth.entities.spells.BasicCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class BasicCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractCircleRenderer<BasicCircleEntity, R> {

    public BasicCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new BasicCircleGeoModel());
    }
}
