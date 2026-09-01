package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class EarthVeinCircleGeoModel extends GeoModel<EarthVeinCircleEntity> {

    private static final Identifier MODEL =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/earth_vein_circle");
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATIONS =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/earth_vein_circle");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(EarthVeinCircleEntity animatable) { return ANIMATIONS; }
}
