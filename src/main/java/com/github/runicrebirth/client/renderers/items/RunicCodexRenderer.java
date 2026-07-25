package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.RunicCodexItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RunicCodexRenderer extends GeoItemRenderer<RunicCodexItem> {

    private static final ResourceLocation GUI_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/item/codex.png");

    public RunicCodexRenderer() {
        super(new RunicCodexModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (displayContext == ItemDisplayContext.GUI) {
            renderFlatIcon(poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }
        super.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderFlatIcon(PoseStack poseStack, MultiBufferSource bufferSource,
                                 int packedLight, int packedOverlay) {
        VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.entityUnlit(GUI_TEXTURE));
        Matrix4f mat = poseStack.last().pose();

        vc.addVertex(mat, 0, 0, 0).setColor(255, 255, 255, 255).setUv(0, 1)
            .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 1, 0, 0).setColor(255, 255, 255, 255).setUv(1, 1)
            .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 1, 1, 0).setColor(255, 255, 255, 255).setUv(1, 0)
            .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 0, 1, 0).setColor(255, 255, 255, 255).setUv(0, 0)
            .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
    }

    @Override
    public RenderType getRenderType(RunicCodexItem animatable, ResourceLocation texture,
                                     @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource,
                                     float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
