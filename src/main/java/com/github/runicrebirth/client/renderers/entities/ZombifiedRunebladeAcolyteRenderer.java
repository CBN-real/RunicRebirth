package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ZombifiedRunebladeAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombifiedRunebladeAcolyteRenderer extends GeoEntityRenderer<ZombifiedRunebladeAcolyteEntity> {

    public ZombifiedRunebladeAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombifiedRunebladeAcolyteGeoModel());
    }
}
