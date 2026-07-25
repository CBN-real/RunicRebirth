package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRunicBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {

    protected AbstractRunicBlockRenderer(BlockEntityRendererProvider.Context context, GeoModel<T> model) {
        super(model);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }


    protected int getPackedLight(T animatable, int packedLight) {
        return packedLight;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        int light = getPackedLight(animatable, packedLight);
        ResourceLocation texture = getTextureLocation(animatable);
        String name = bone.getName();

//        if (name.endsWith("_es")) {
//            RenderType swirlType = RenderType.energySwirl(texture, 0, 0);
//            VertexConsumer swirlBuffer = bufferSource.getBuffer(swirlType);
//            super.renderRecursively(poseStack, animatable, bone, swirlType, bufferSource, swirlBuffer,
//                isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
//            return;
//        }

        RenderType noShadeType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
        VertexConsumer noShadeBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(noShadeType));
        super.renderRecursively(poseStack, animatable, bone, noShadeType, bufferSource, noShadeBuffer,
            isReRender, partialTick, light, packedOverlay, colour);
    }
}
