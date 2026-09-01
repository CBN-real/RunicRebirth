package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonSwingingAxeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class DungeonSwingingAxeRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<DungeonSwingingAxeBlockEntity, R> {
    // TODO GeckoLib 5: renderRecursively/preRender/postRender/getRenderType signatures changed.
    // In GeckoLib 5: use GeoRenderLayer for per-bone render types; addRenderData() for pre-render hooks;
    // adjustModelBonesForRender(RenderPassInfo, BoneSnapshots) for bone manipulation.
    // These overrides need to be migrated or removed before this file compiles.

    public DungeonSwingingAxeRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonSwingingAxeModel());
    }
    // TODO GeckoLib 5: renderRecursively() removed. Migrate to GeckoLib 5 equivalent.
    // Original body preserved for reference:
    // @Override
    //     public void renderRecursively(PoseStack poseStack, DungeonSwingingAxeBlockEntity animatable, GeoBone bone,
    //             RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
    //             boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
    //
    //         if ("gear_assembly".equals(bone.getName())) {
    //             Level level = animatable.getLevel();
    //             if (level != null) {
    //                 double gameTick = (level.getGameTime() % DungeonSwingingAxeBlockEntity.FULL_CYCLE) + partialTick;
    //                 float rotZ = (float) Math.toRadians(
    //                         Math.sin(Math.PI * gameTick / DungeonSwingingAxeBlockEntity.HALF_CYCLE)
    //                         * DungeonSwingingAxeBlockEntity.MAX_ANGLE_DEG);
    //                 bone.setRotZ(rotZ);
    //             }
    //         }
    //
    //         super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
    //                 isReRender, partialTick, packedLight, packedOverlay, colour);
    //     }

}
