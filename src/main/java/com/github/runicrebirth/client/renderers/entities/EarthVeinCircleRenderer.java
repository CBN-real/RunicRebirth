package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.EarthVeinCircleGeoModel;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EarthVeinCircleRenderer extends GeoEntityRenderer<EarthVeinCircleEntity> {

    public EarthVeinCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new EarthVeinCircleGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public @Nullable RenderType getRenderType(EarthVeinCircleEntity animatable,
        ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
