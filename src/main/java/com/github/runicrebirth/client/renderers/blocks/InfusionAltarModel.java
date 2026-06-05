package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;

@OnlyIn(Dist.CLIENT)
public class InfusionAltarModel extends GeoModel<InfusionAltarBlockEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/block/infusion_altar.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/block/infusion_altar.animation.json");

    @Override
    public ResourceLocation getModelResource(InfusionAltarBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(InfusionAltarBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(InfusionAltarBlockEntity animatable) {
        return ANIMATION;
    }
}
