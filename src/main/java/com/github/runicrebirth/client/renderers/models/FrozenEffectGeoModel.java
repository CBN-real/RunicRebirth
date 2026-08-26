package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrozenEffectGeoModel extends GeoModel<FrozenEffectEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "geo/entity/frozen_effect.geo.json");

    private static final ResourceLocation[] TEX_FRAMES = new ResourceLocation[4];
    static {
        for (int i = 0; i < 4; i++) {
            TEX_FRAMES[i] = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/entity/effect/frozen_effect" + (i + 1) + ".png");
        }
    }

    @Override
    public ResourceLocation getModelResource(FrozenEffectEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(FrozenEffectEntity entity) {
        int ticksPerFrame = Math.max(1, entity.freezeDurationTicks / 4);
        int frame = Math.min(3, entity.tickCount / ticksPerFrame);
        return TEX_FRAMES[frame];
    }

    @Override
    public ResourceLocation getAnimationResource(FrozenEffectEntity entity) { return null; }
}
