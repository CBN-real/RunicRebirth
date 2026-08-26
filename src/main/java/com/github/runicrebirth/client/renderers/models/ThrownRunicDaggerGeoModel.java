package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThrownRunicDaggerGeoModel extends GeoModel<ThrownRunicDaggerEntity> {
  private static final ResourceLocation MODEL =
      ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/runic_dagger.geo.json");
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_dagger_texture.png");
  private static final ResourceLocation ANIMATIONS =
      ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/runic_dagger.animation.json");

    @Override public ResourceLocation getModelResource(ThrownRunicDaggerEntity e) { return MODEL; }
    @Override public ResourceLocation getTextureResource(ThrownRunicDaggerEntity e) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(ThrownRunicDaggerEntity e) { return ANIMATIONS; }
}
