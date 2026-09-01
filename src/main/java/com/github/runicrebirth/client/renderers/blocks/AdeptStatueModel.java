package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

// TODO GeckoLib 5: getModelResource previously selected model by block type from AdeptStatueBlockEntity.getBlockState().
// Migrate: create AdeptStatueRenderState extends BlockEntityRenderState & GeoRenderState with a block-type enum field;
// fill via addRenderData() in AdeptStatueRenderer; switch on that field here.
public class AdeptStatueModel extends GeoModel {

    private static final Identifier GEO_MAGE      = rl("block/adept_mage_set_statue");
    private static final Identifier GEO_WIZARD    = rl("block/adept_wizard_set_statue");
    private static final Identifier GEO_RUNEBLADE = rl("block/adept_runeblade_armor_statue");
    private static final Identifier GEO_ARTIFICER = rl("block/adept_artificer_set_statue");
    private static final Identifier TEX_SHARED    = rl("textures/block/adept_armor_statue_texture.png");

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, path);
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: select model based on AdeptStatueRenderState statue type (see class comment)
        return GEO_MAGE;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEX_SHARED; }

    @Override
    public Identifier getAnimationResource(com.geckolib.animatable.GeoAnimatable animatable) { return null; }
}
