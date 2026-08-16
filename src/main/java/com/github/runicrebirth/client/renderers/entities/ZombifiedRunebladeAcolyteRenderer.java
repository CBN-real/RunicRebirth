package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ZombifiedRunebladeAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombifiedRunebladeAcolyteRenderer extends GeoEntityRenderer<ZombifiedRunebladeAcolyteEntity> {

    public ZombifiedRunebladeAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombifiedRunebladeAcolyteGeoModel());
    }

    @Override
    public @Nullable RenderType getRenderType(ZombifiedRunebladeAcolyteEntity animatable,
        ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
