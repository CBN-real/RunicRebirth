package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonPressurePlateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DungeonPressurePlateRenderer implements BlockEntityRenderer<DungeonPressurePlateBlockEntity, DungeonPressurePlateRenderer.RenderState> {

    public static class RenderState extends BlockEntityRenderState {}

    public DungeonPressurePlateRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(DungeonPressurePlateBlockEntity be, RenderState state, float partialTicks,
                                    Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        // stub - rendering omitted
    }
}
