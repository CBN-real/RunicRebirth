package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.InfusionCircleGeoModel;
import com.github.runicrebirth.entities.spells.InfusionCircleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfusionCircleRenderer extends AbstractSpellRenderer<InfusionCircleEntity> {

    public InfusionCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new InfusionCircleGeoModel());
    }
}
