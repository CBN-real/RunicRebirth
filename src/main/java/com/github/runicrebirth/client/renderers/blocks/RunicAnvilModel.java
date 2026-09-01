package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class RunicAnvilModel extends GeoModel<RunicAnvilBlockEntity> {

    private static final Identifier MODEL =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/runic_anvil");
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATION =
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/runic_anvil");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(RunicAnvilBlockEntity animatable) {
        return ANIMATION;
    }
}
