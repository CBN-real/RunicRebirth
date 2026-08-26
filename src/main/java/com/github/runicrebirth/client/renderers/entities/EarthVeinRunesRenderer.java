package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.EarthVeinRunesGeoModel;
import com.github.runicrebirth.entities.EarthVeinRunesEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class EarthVeinRunesRenderer extends GeoEntityRenderer<EarthVeinRunesEntity> {

    public EarthVeinRunesRenderer(EntityRendererProvider.Context context) {
        super(context, new EarthVeinRunesGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public @Nullable RenderType getRenderType(EarthVeinRunesEntity animatable,
        ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
    }
}
