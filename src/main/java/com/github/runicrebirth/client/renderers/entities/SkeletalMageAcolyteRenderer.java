package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.SkeletalMageAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletalMageAcolyteRenderer extends GeoEntityRenderer<SkeletalMageAcolyteEntity> {

    public SkeletalMageAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletalMageAcolyteGeoModel());
    }
}
