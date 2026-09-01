package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RunicBlockItemModel<T extends GeoAnimatable> extends GeoModel<T> {

    private final Identifier model;
    private final Identifier texture;
    private final Identifier animation;

    public RunicBlockItemModel(String geoPath, String texturePath, String animationPath) {
        this.model = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, geoPath);
        this.texture = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, texturePath);
        this.animation = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, animationPath);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return model; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return texture; }

    @Override
    public Identifier getAnimationResource(T animatable) { return animation; }
}
