package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonDoorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.model.GeoModel;

public class DungeonDoorModel extends GeoModel<DungeonDoorBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "geo/block/dungeon_door.geo.json");
    private static final ResourceLocation TEX_STATIC = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/block/dungeon_door.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "animations/block/dungeon_door.animation.json");

    private static final ResourceLocation[] TEX_ANIM_FRAMES   = new ResourceLocation[8];
    private static final ResourceLocation[] TEX_CLOSING_FRAMES = new ResourceLocation[8];

    static {
        for (int i = 0; i < 8; i++) {
            TEX_ANIM_FRAMES[i]    = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/dungeon_door"    + i + ".png");
            TEX_CLOSING_FRAMES[i] = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/dungeon_door_closing" + i + ".png");
        }
    }

    @Override
    public ResourceLocation getModelResource(DungeonDoorBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DungeonDoorBlockEntity animatable) {
        return switch (animatable.getAnimState()) {
            case OPENING -> {
                yield TEX_ANIM_FRAMES[animFrame(animatable)];
            }
            case CLOSING -> {
                yield TEX_CLOSING_FRAMES[animFrame(animatable)];
            }
            case CLOSED, OPEN -> TEX_STATIC;
        };
    }

    private static int animFrame(DungeonDoorBlockEntity animatable) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return 0;
        long elapsed = level.getGameTime() - animatable.getTransitionStartTime();
        return (int) Math.max(0, Math.min(7, elapsed / 8));
    }

    @Override
    public ResourceLocation getAnimationResource(DungeonDoorBlockEntity animatable) {
        return ANIMATION;
    }
}
