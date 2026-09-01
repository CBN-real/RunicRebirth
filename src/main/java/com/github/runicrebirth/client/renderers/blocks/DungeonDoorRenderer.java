package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.blocks.entity.DungeonDoorBlockEntity;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import com.geckolib.renderer.base.GeoRenderState;

public class DungeonDoorRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends AbstractRunicBlockRenderer<DungeonDoorBlockEntity, R> {

    public DungeonDoorRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonDoorModel());
    }

    // TODO GeckoLib 5: door facing rotation (was in preRender) must move to adjustRenderPose(RenderPassInfo<R>).
    // Requires custom DungeonDoorRenderState extends BlockEntityRenderState & GeoRenderState
    // capturing DungeonDoorBlock.FACING in extractRenderState(), then reading it here:
    //   Direction facing = renderState.facing;
    //   poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
    //   rotateBlock(facing, poseStack);
}
