package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicSlashEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicSlashGeoModel extends GeoModel<MagicSlashEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/magic_slash.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/spell_boundary/arcane_boundary_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/magic_slash.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicSlashEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicSlashEntity entity) {
        if (entity != null) {
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
                return ResourceLocation.fromNamespaceAndPath(
                    InteractiveMagic.MODID,
                    "textures/spell_boundary/" + parsed.getPath() + "_boundary_texture.png"
                );
            }
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicSlashEntity entity) {
        return ANIMATIONS;
    }
}
