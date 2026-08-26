package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.RunicWarstaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunicWarstaffModel extends GeoModel<RunicWarstaffItem> {
    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/runic_warstaff.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/models/weapon/runic_warstaff_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/runic_warstaff.animation.json");

    @Override public ResourceLocation getModelResource(RunicWarstaffItem a) { return MODEL; }
    @Override public ResourceLocation getTextureResource(RunicWarstaffItem a) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(RunicWarstaffItem a) { return ANIMATIONS; }
}
