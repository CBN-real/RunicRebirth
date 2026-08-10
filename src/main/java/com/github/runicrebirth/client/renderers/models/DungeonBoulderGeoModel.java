package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.DungeonBoulderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DungeonBoulderGeoModel extends GeoModel<DungeonBoulderEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/entity/dungeon_boulder.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/earth_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/entity/dungeon_boulder.animation.json");

    @Override
    public ResourceLocation getModelResource(DungeonBoulderEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DungeonBoulderEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DungeonBoulderEntity animatable) {
        return ANIMATION;
    }
}
