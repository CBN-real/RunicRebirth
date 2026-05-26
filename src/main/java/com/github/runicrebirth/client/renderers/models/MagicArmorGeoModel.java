package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;

@OnlyIn(Dist.CLIENT)
public class MagicArmorGeoModel<T extends MagicArmorItem> extends GeoModel<T> {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public MagicArmorGeoModel(String name) {
        this.model = ResourceLocation.fromNamespaceAndPath(
                RunicRebirth.MODID, "geo/armor/" + name + ".geo.json");
        this.texture = ResourceLocation.fromNamespaceAndPath(
                RunicRebirth.MODID, "textures/models/armor/" + name + "_texture.png");
        this.animation = ResourceLocation.fromNamespaceAndPath(
                RunicRebirth.MODID, "animations/armor/" + name + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T item) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T item) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T item) {
        return animation;
    }
}
