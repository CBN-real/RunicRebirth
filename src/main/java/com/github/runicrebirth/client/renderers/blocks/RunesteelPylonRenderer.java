package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunesteelPylonBlockEntity;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import org.jetbrains.annotations.Nullable;

public class RunesteelPylonRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<RunesteelPylonBlockEntity, R> {

    public RunesteelPylonRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunesteelPylonModel());
    }

    @Override
    public void captureDefaultRenderState(RunesteelPylonBlockEntity animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        super.captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);

        int packedLight = renderState.getPackedLight();
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, LightCoordsUtil.pack(
            Math.max(LightCoordsUtil.block(packedLight), 7),
            LightCoordsUtil.sky(packedLight)
        ));
    }
}
