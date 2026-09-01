package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class InfusionAltarModel extends GeoModel<InfusionAltarBlockEntity> {

    private static final Identifier MODEL =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/infusion_altar");
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATION =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/infusion_altar");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(InfusionAltarBlockEntity animatable) {
        return ANIMATION;
    }
}
