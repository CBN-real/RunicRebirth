package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ZombifiedArtificerAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombifiedArtificerAcolyteRenderer extends GeoEntityRenderer<ZombifiedArtificerAcolyteEntity> {

    public ZombifiedArtificerAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombifiedArtificerAcolyteGeoModel());
    }

    @Override
    public @Nullable RenderType getRenderType(ZombifiedArtificerAcolyteEntity animatable,
        ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
    }
}
