package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.AcolyteWandItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AcolyteWandModel extends GeoModel<AcolyteWandItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/acolyte_wand.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/item/acolyte_wand_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/acolyte_wand.animation.json");

    @Override
    public ResourceLocation getModelResource(AcolyteWandItem animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(AcolyteWandItem animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(AcolyteWandItem animatable) { return ANIMATIONS; }
}
