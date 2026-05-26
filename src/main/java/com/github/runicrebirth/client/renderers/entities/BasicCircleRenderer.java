package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.BasicCircleGeoModel;
import com.github.runicrebirth.entities.spells.BasicCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicCircleRenderer extends AbstractCircleRenderer<BasicCircleEntity> {

    public BasicCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new BasicCircleGeoModel());
    }
}
