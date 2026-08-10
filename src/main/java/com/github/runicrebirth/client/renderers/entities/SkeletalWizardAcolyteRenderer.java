package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.SkeletalWizardAcolyteGeoModel;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletalWizardAcolyteRenderer extends GeoEntityRenderer<SkeletalWizardAcolyteEntity> {

    public SkeletalWizardAcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletalWizardAcolyteGeoModel());
    }

  @Override
  public @Nullable RenderType getRenderType(SkeletalWizardAcolyteEntity animatable,
      ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
    return RenderType.entityTranslucent(texture);
  }
}
