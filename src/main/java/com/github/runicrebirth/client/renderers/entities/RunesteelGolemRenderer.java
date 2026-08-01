package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.RunesteelGolemGeoModel;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RunesteelGolemRenderer extends GeoEntityRenderer<RunesteelGolemEntity> {

    public RunesteelGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new RunesteelGolemGeoModel());
    }
}
