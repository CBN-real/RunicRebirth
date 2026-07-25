package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.RunicCodexItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunicCodexModel extends GeoModel<RunicCodexItem> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/item/runic_codex.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/item/codex_model_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/item/runic_codex.animation.json");

    @Override
    public ResourceLocation getModelResource(RunicCodexItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RunicCodexItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunicCodexItem animatable) {
        return ANIMATIONS;
    }
}
