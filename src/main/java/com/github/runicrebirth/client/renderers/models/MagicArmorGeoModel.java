package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicArmorGeoModel<T extends MagicArmorItem> extends GeoModel<T> {

    private final Identifier model;
    private final Identifier texture;
    private final Identifier animation;

    public MagicArmorGeoModel(String name) {
        this(name, name);
    }

    public MagicArmorGeoModel(String modelName, String textureName) {
        this.model = Identifier.fromNamespaceAndPath(
                RunicRebirth.MODID, "armor/" + modelName);
        this.texture = Identifier.fromNamespaceAndPath(
                RunicRebirth.MODID, "textures/models/armor/" + textureName + "_texture.png");
        this.animation = Identifier.fromNamespaceAndPath(
                RunicRebirth.MODID, "armor/" + modelName);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return animation;
    }
}
