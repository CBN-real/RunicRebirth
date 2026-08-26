package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.EarthVeinRunesEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EarthVeinRunesGeoModel extends GeoModel<EarthVeinRunesEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/earth_vein_runes.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/block/earth_vein_runes.animation.json");

    @Override
    public ResourceLocation getModelResource(EarthVeinRunesEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(EarthVeinRunesEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(EarthVeinRunesEntity entity) { return ANIMATIONS; }
}
