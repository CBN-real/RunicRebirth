package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonSwingingAxeBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class DungeonSwingingAxeModel extends GeoModel<DungeonSwingingAxeBlockEntity> {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/dungeon_swinging_axe");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/earth_runic_template.png");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/dungeon_swinging_axe");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(DungeonSwingingAxeBlockEntity animatable) {
        return ANIMATION;
    }
}
