package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.AdeptStaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AdeptStaffModel extends GeoModel<AdeptStaffItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/adept_staff.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/adept_staff.animation.json");

    @Override
    public ResourceLocation getModelResource(AdeptStaffItem animatable) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(AdeptStaffItem animatable) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(AdeptStaffItem animatable) { return ANIMATIONS; }
}
