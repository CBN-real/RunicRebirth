package com.github.interactivemagic.client.renderers.items;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.items.BasicWandItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BasicWandModel extends GeoModel<BasicWandItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/item/basic_wand.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/item/basic_wand_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/item/basic_wand.animation.json");

    @Override
    public ResourceLocation getModelResource(BasicWandItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BasicWandItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BasicWandItem animatable) {
        return ANIMATIONS;
    }
}
