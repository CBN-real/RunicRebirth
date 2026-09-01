package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.curios.HammerDroneItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class HammerDroneItemModel extends GeoModel<HammerDroneItem> {

    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/hammer_drone");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/hammer_drone");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(HammerDroneItem animatable) { return ANIMATIONS; }
}
