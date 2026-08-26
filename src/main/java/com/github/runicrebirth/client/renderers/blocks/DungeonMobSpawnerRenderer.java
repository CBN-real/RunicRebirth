package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DungeonMobSpawnerRenderer extends GeoBlockRenderer<DungeonMobSpawnerBlockEntity> {

    public DungeonMobSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(new DungeonMobSpawnerModel());
    }

  @Override
  public @Nullable RenderType getRenderType(DungeonMobSpawnerBlockEntity animatable,
      ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
    return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
  }

  @Override
    public void renderRecursively(PoseStack poseStack, DungeonMobSpawnerBlockEntity animatable,
                                   GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
                                   VertexConsumer buffer, boolean isReRender, float partialTick,
                                   int packedLight, int packedOverlay, int colour) {
        if ("dungeon_mob_spawner".equals(bone.getName())) {
            // Scale the spawn circle bone based on configured spawn radius.
            // radius 0.5 = scale 1.0 (model default = 1 block diameter), so scale = radius * 2.
            float scale = animatable.getSpawnRadius() * 2.0f;
            bone.setScaleX(scale);
            bone.setScaleZ(scale);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
