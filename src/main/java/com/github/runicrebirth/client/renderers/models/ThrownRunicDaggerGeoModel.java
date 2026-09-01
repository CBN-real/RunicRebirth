package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class ThrownRunicDaggerGeoModel extends GeoModel<ThrownRunicDaggerEntity> {
  private static final Identifier MODEL =
      Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/runic_dagger");
  private static final Identifier TEXTURE =
      Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_dagger_texture.png");
  private static final Identifier ANIMATIONS =
      Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/runic_dagger");

    @Override public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }
    @Override public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }
    @Override public Identifier getAnimationResource(ThrownRunicDaggerEntity animatable) { return ANIMATIONS; }
}
