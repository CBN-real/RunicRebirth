package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.MagicBindingEntity;
import com.github.runicrebirth.entities.spells.MagicBlastEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicBlastGeoModel extends GeoModel<MagicBlastEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/magic_blast.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/magic_blast.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicBlastEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicBlastEntity entity) {
      if (entity != null) {
        String elementId = entity.getElementId();
        ResourceLocation parsed = ResourceLocation.tryParse(elementId);
        if (parsed != null) {
          return ResourceLocation.fromNamespaceAndPath(
              RunicRebirth.MODID,
              "textures/entity/runic_templates/" + parsed.getPath() + "_runic_template.png"
          );
        }
      }
      return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicBlastEntity entity) {
        return ANIMATIONS;
    }
}
