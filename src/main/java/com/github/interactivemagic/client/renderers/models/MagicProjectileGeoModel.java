package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicProjectileGeoModel extends GeoModel<MagicProjectileEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/magic_projectile.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/entity/magic_projectile/arcane_magic_projectile_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/magic_projectile.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicProjectileEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicProjectileEntity entity) {
        if (entity != null) {
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
                return ResourceLocation.fromNamespaceAndPath(
                    InteractiveMagic.MODID,
                    "textures/entity/magic_projectile/" + parsed.getPath() + "_magic_projectile_texture.png"
                );
            }
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicProjectileEntity entity) {
        return ANIMATIONS;
    }
}
