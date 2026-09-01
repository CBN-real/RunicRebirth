package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

// TODO GeckoLib 5: getModelResource previously selected model by AttachFace from block state.
// Migrate: create AncientArcaneTurretRenderState extends BlockEntityRenderState & GeoRenderState with attachFace field;
// fill via addRenderData() in AncientArcaneTurretRenderer; switch on it here.
public class AncientArcaneTurretModel extends GeoModel {

    private static final Identifier FLOOR_MODEL   = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/ancient_arcane_turret_floor");
    private static final Identifier CEILING_MODEL = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/ancient_arcane_turret_ceiling");
    private static final Identifier WALL_MODEL    = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/ancient_arcane_turret_wall");
    private static final Identifier TEXTURE       = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATION     = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/ancient_arcane_turret");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: select FLOOR/CEILING/WALL model from AncientArcaneTurretRenderState.attachFace (see class comment)
        return FLOOR_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(com.geckolib.animatable.GeoAnimatable animatable) { return ANIMATION; }
}
