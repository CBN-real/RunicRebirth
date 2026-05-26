package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.MagicBeamEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicBeamGeoModel extends GeoModel<MagicBeamEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/magic_beam.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/magic_beam/arcane_magic_beam_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/magic_beam.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicBeamEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicBeamEntity entity) {
      if (entity != null) {
        String elementId = entity.getElementId();
        ResourceLocation parsed = ResourceLocation.tryParse(elementId);
        if (parsed != null) {
          return ResourceLocation.fromNamespaceAndPath(
              RunicRebirth.MODID,
              "textures/entity/magic_beam/" + parsed.getPath() + "_magic_beam_texture.png"
          );
        }
      }
      return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicBeamEntity entity) {
        return ANIMATIONS;
    }
}
