package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.AdvancedCircleGeoModel;
import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedCircleRenderer extends AbstractCircleRenderer<AdvancedCircleEntity> {

    public AdvancedCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new AdvancedCircleGeoModel());
    }
}
