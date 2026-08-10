package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.curios.ArcaneDroneItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArcaneDroneItemModel extends GeoModel<ArcaneDroneItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/arcane_drone.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/arcane_drone.animation.json");

    @Override
    public ResourceLocation getModelResource(ArcaneDroneItem animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(ArcaneDroneItem animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(ArcaneDroneItem animatable) { return ANIMATIONS; }
}
