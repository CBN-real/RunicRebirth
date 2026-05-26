package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.IntermediateCircleGeoModel;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntermediateCircleRenderer extends AbstractCircleRenderer<IntermediateCircleEntity> {

    public IntermediateCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new IntermediateCircleGeoModel());
    }
}
