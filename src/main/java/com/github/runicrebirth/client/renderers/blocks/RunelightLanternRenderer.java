package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunelightLanternBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class RunelightLanternRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<RunelightLanternBlockEntity, R> {
    // TODO GeckoLib 5: renderRecursively/preRender/postRender/getRenderType signatures changed.
    // In GeckoLib 5: use GeoRenderLayer for per-bone render types; addRenderData() for pre-render hooks;
    // adjustModelBonesForRender(RenderPassInfo, BoneSnapshots) for bone manipulation.
    // These overrides need to be migrated or removed before this file compiles.

    public RunelightLanternRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunelightLanternModel());
    }

    @Override
    public AABB getRenderBoundingBox(RunelightLanternBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
    // TODO GeckoLib 5: renderRecursively() removed. Migrate to GeckoLib 5 equivalent.
    // Original body preserved for reference:
    // @Override
    //     public void renderRecursively(PoseStack poseStack, RunelightLanternBlockEntity animatable, GeoBone bone,
    //         RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
    //         boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
    //
    //         Identifier texture = getTextureLocation(animatable);
    //         RenderType type = RenderTypes.entityTranslucent(texture);
    //         VertexConsumer consumer = bufferSource.getBuffer(type);
    //         super.renderRecursively(poseStack, animatable, bone, type, bufferSource, consumer,
    //             isReRender, partialTick, 15728880, packedOverlay, colour);
    //     }

    // TODO GeckoLib 5: preRender() removed. Migrate to GeckoLib 5 equivalent.
    // Original body preserved for reference:
    // @Override
    //     public void preRender(PoseStack poseStack, RunelightLanternBlockEntity animatable,
    //         BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
    //         @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
    //         int packedOverlay, int colour) {
    //
    //         for (int i = 1; i <= 8; i++) {
    //             Optional<GeoBone> bone = model.getBone("rune_" + i + "_s1");
    //             if (i != animatable.getSelectedRune()) {
    //                 bone.ifPresent(geoBone -> geoBone.setHidden(true));
    //             } else {
    //                 bone.ifPresent(geoBone -> geoBone.setHidden(false));
    //             }
    //         }
    //
    //         super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
    //             packedLight, packedOverlay, colour);
    //     }

}
