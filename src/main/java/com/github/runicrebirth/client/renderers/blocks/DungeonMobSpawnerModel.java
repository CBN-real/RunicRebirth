package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DungeonMobSpawnerModel extends GeoModel<DungeonMobSpawnerBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/dungeon_mob_spawner.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/dungeon_mob_spawner.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/dungeon_mob_spawner.animation.json");

    @Override
    public ResourceLocation getModelResource(DungeonMobSpawnerBlockEntity animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(DungeonMobSpawnerBlockEntity animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(DungeonMobSpawnerBlockEntity animatable) { return ANIMATION; }
}
