package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.RunicShieldItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RunicShieldModel extends GeoModel<RunicShieldItem> {
    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/runic_shield");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_shield_texture.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/runic_shield");

    @Override public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }
    @Override public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }
    @Override public Identifier getAnimationResource(RunicShieldItem animatable) { return ANIMATIONS; }
}
