package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.client.animations.DaggerAnimLayer;
import com.github.runicrebirth.items.RunicDaggerItem;
import com.github.runicrebirth.network.DaggerAnimS2CPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@OnlyIn(Dist.CLIENT)
public class RunicDaggerRenderer extends GeoItemRenderer<RunicDaggerItem> {
    public RunicDaggerRenderer() { super(new RunicDaggerModel()); }

    @Override
    public RenderType getRenderType(RunicDaggerItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, RunicDaggerItem animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        Minecraft mc = Minecraft.getInstance();
        boolean isHandContext = renderPerspective == net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || renderPerspective == net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        final boolean thrown = isHandContext && mc.player != null
            && DaggerAnimLayer.getAnim(mc.player.getId()) != DaggerAnimS2CPacket.Anim.IDLE;
        model.getBone("runic_dagger").ifPresent(b -> b.setHidden(thrown));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
