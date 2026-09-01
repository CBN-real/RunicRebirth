package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.TatteredSectBannerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class TatteredSectBannerRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<TatteredSectBannerBlockEntity, R> {
    // TODO GeckoLib 5: banner pose (applyBannerPose) and pattern layers (renderRecursively) need migrating
    // to extractRenderState() + GeoRenderLayer respectively.

    public TatteredSectBannerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new TatteredSectBannerModel());
    }
}
