package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ZombifiedArtificerAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombifiedArtificerAcolyteRenderer extends GeoEntityRenderer<ZombifiedArtificerAcolyteEntity> {

    public ZombifiedArtificerAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombifiedArtificerAcolyteGeoModel());
    }
}
