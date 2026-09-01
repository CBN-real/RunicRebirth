package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.github.runicrebirth.client.renderers.entities.FrozenEffectRenderer;

public class FrozenEffectGeoModel extends GeoModel<FrozenEffectEntity> {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "entity/frozen_effect");

    private static final Identifier[] TEX_FRAMES = new Identifier[4];
    static {
        for (int i = 0; i < 4; i++) {
            TEX_FRAMES[i] = Identifier.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/entity/effect/frozen_effect" + (i + 1) + ".png");
        }
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int duration = renderState.getOrDefaultGeckolibData(FrozenEffectRenderer.FROZEN_DURATION, 40);
        int tick = renderState.getOrDefaultGeckolibData(FrozenEffectRenderer.FROZEN_TICK, 0);
        int ticksPerFrame = Math.max(1, duration / 4);
        int frame = Math.min(3, tick / ticksPerFrame);
        return TEX_FRAMES[frame];
    }

    @Override
    public Identifier getAnimationResource(FrozenEffectEntity animatable) { return null; }
}
