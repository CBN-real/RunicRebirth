package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.curios.HammerDroneItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HammerDroneItemModel extends GeoModel<HammerDroneItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/hammer_drone.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/hammer_drone.animation.json");

    @Override
    public ResourceLocation getModelResource(HammerDroneItem animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(HammerDroneItem animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(HammerDroneItem animatable) { return ANIMATIONS; }
}
