package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.AbstractSectBannerBlock;
import com.github.runicrebirth.blocks.entity.TatteredSectBannerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import software.bernie.geckolib.model.GeoModel;

public class TatteredSectBannerModel extends GeoModel<TatteredSectBannerBlockEntity> {

    private static final ResourceLocation MODEL_FLOOR   = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/block/tattered_sect_banner.geo.json");
    private static final ResourceLocation MODEL_WALL    = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/block/tattered_sect_banner_wall.geo.json");
    private static final ResourceLocation MODEL_CEILING = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/block/tattered_sect_banner_ceiling.geo.json");
    private static final ResourceLocation TEXTURE   = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/tattered_sect_banner.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/block/sect_banner.animation.json");

    @Override
    public ResourceLocation getModelResource(TatteredSectBannerBlockEntity a) {
        AttachFace face = a.getBlockState().getValue(AbstractSectBannerBlock.FACE);
        return switch (face) {
            case WALL    -> MODEL_WALL;
            case CEILING -> MODEL_CEILING;
            default      -> MODEL_FLOOR;
        };
    }

    @Override public ResourceLocation getTextureResource(TatteredSectBannerBlockEntity a) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(TatteredSectBannerBlockEntity a) { return ANIMATION; }
}
