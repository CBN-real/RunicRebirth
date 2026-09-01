package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class DungeonMobSpawnerRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<DungeonMobSpawnerBlockEntity, R> {
    // TODO GeckoLib 5: renderRecursively/preRender/postRender/getRenderType signatures changed.
    // In GeckoLib 5: use GeoRenderLayer for per-bone render types; addRenderData() for pre-render hooks;
    // adjustModelBonesForRender(RenderPassInfo, BoneSnapshots) for bone manipulation.
    // These overrides need to be migrated or removed before this file compiles.

    public DungeonMobSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonMobSpawnerModel());
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
    // TODO GeckoLib 5: renderRecursively() removed. Migrate to GeckoLib 5 equivalent.
    // Original body preserved for reference:
    // @Override
    //     public void renderRecursively(PoseStack poseStack, DungeonMobSpawnerBlockEntity animatable,
    //                                    GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
    //                                    VertexConsumer buffer, boolean isReRender, float partialTick,
    //                                    int packedLight, int packedOverlay, int colour) {
    //         if ("dungeon_mob_spawner".equals(bone.getName())) {
    //             float scale = animatable.getSpawnRadius() * 2.0f;
    //             bone.setScaleX(scale);
    //             bone.setScaleZ(scale);
    //         }
    //         super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
    //                 buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    //     }

}
