package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

// TODO GeckoLib 5: getTextureResource previously selected animated frame textures from DungeonDoorBlockEntity state.
// Migrate: create DungeonDoorRenderState extends BlockEntityRenderState & GeoRenderState, add animState +
// transitionStartTime fields; fill via addRenderData() in DungeonDoorRenderer; cast renderState here.
public class DungeonDoorModel extends GeoModel {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "block/dungeon_door");
    private static final Identifier TEX_STATIC = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/block/dungeon_door.png");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "block/dungeon_door");

    private static final Identifier[] TEX_ANIM_FRAMES   = new Identifier[8];
    private static final Identifier[] TEX_CLOSING_FRAMES = new Identifier[8];

    static {
        for (int i = 0; i < 8; i++) {
            TEX_ANIM_FRAMES[i]    = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/dungeon_door"    + i + ".png");
            TEX_CLOSING_FRAMES[i] = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/dungeon_door_closing" + i + ".png");
        }
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: select TEX_ANIM_FRAMES / TEX_CLOSING_FRAMES based on DungeonDoorRenderState (see class comment)
        return TEX_STATIC;
    }

    @Override
    public Identifier getAnimationResource(com.geckolib.animatable.GeoAnimatable animatable) { return ANIMATION; }
}
