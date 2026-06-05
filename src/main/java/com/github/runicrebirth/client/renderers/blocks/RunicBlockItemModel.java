package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.RunicBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunicBlockItemModel extends GeoModel<RunicBlockItem> {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public RunicBlockItemModel(String geoPath, String texturePath, String animationPath) {
        this.model = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, geoPath);
        this.texture = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, texturePath);
        this.animation = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, animationPath);
    }

    @Override
    public ResourceLocation getModelResource(RunicBlockItem animatable) { return model; }

    @Override
    public ResourceLocation getTextureResource(RunicBlockItem animatable) { return texture; }

    @Override
    public ResourceLocation getAnimationResource(RunicBlockItem animatable) { return animation; }
}
