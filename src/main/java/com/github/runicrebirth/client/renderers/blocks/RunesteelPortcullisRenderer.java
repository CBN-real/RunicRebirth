package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.RunesteelPortcullisBlock;
import com.github.runicrebirth.blocks.entity.RunesteelPortcullisBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class RunesteelPortcullisRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends AbstractRunicBlockRenderer<RunesteelPortcullisBlockEntity, R> {

    public RunesteelPortcullisRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunesteelPortcullisModel());
    }

    // TODO GeckoLib 5: facing rotation (was in render() poseStack manipulation) must move to
    // adjustRenderPose(RenderPassInfo<R>). Requires custom RenderState capturing FACING from block state.

    // TODO GeckoLib 5: portcullis bone visibility (was in renderRecursively) must move to
    // adjustModelBonesForRender(RenderPassInfo<R>, BoneSnapshots). Requires custom RenderState capturing
    // RunesteelPortcullisBlock.OPEN and HEIGHT in extractRenderState(). Logic:
    //   snapshots.ifPresent(boneName, bone -> {
    //       bone.setHidden(open || idx > height);
    //   });
    // Iterate all bone names with "runesteel_portcullis_middle" prefix via model().getBone().
}
