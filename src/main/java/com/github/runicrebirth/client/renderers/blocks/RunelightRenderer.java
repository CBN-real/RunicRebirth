package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunelightBlockEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@OnlyIn(Dist.CLIENT)
public class RunelightRenderer extends GeoBlockRenderer<RunelightBlockEntity> {

    public RunelightRenderer(BlockEntityRendererProvider.Context context) {
        super(new RunelightModel());
    }

    @Override
    public AABB getRenderBoundingBox(RunelightBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public RenderType getRenderType(RunelightBlockEntity animatable, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RunelightBlockEntity animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);
        RenderType noShadeType = RenderType.entityTranslucent(texture);
        VertexConsumer noShadeBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(noShadeType));
        super.renderRecursively(poseStack, animatable, bone, noShadeType, bufferSource, noShadeBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }

    @Override
    public void preRender(PoseStack poseStack, RunelightBlockEntity animatable,
        BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
        @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
        int packedOverlay, int colour) {

        for (int i = 1; i <= 8; i++) {
            Optional<GeoBone> bone = model.getBone("rune_" + i + "_s1");
            if (i != animatable.getSelectedRune()) {
                bone.ifPresent(geoBone -> geoBone.setHidden(true));
            } else {
                bone.ifPresent(geoBone -> geoBone.setHidden(false));
            }
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
            packedLight, packedOverlay, colour);
    }
}
