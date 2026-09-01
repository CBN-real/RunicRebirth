package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.InfusionCircleGeoModel;
import com.github.runicrebirth.entities.spells.InfusionCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class InfusionCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<InfusionCircleEntity, R> {

    public InfusionCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new InfusionCircleGeoModel());
    }
}
