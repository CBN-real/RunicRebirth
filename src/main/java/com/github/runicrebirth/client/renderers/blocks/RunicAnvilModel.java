package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;

@OnlyIn(Dist.CLIENT)
public class RunicAnvilModel extends GeoModel<RunicAnvilBlockEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/block/runic_anvil.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/block/runic_anvil.animation.json");

    @Override
    public ResourceLocation getModelResource(RunicAnvilBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RunicAnvilBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunicAnvilBlockEntity animatable) {
        return ANIMATION;
    }
}
