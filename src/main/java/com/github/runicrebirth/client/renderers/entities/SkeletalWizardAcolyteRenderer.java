package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.SkeletalWizardAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletalWizardAcolyteRenderer extends GeoEntityRenderer<SkeletalWizardAcolyteEntity> {

    public SkeletalWizardAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletalWizardAcolyteGeoModel());
    }
}
