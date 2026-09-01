package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.AdeptStaffItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class AdeptStaffModel extends GeoModel<AdeptStaffItem> {

    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/adept_staff");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "item/adept_staff");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(AdeptStaffItem animatable) { return ANIMATIONS; }
}
