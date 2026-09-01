package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.AdvancedCircleGeoModel;
import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class AdvancedCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractCircleRenderer<AdvancedCircleEntity, R> {

    public AdvancedCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new AdvancedCircleGeoModel());
    }
}
