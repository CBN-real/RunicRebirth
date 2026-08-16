package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.BasicRunicLongsword;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BasicRunicLongswordModel extends GeoModel<BasicRunicLongsword> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/runic_longsword.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_longsword.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/basic_runic_longsword.animation.json");

    @Override
    public ResourceLocation getModelResource(BasicRunicLongsword animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(BasicRunicLongsword animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(BasicRunicLongsword animatable) { return ANIMATIONS; }
}
