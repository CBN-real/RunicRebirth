package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.RunicShieldItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunicShieldModel extends GeoModel<RunicShieldItem> {
    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/runic_shield.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_shield_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/runic_shield.animation.json");

    @Override public ResourceLocation getModelResource(RunicShieldItem a) { return MODEL; }
    @Override public ResourceLocation getTextureResource(RunicShieldItem a) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(RunicShieldItem a) { return ANIMATIONS; }
}
