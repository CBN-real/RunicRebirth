package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.renderers.models.MagicShieldGeoModel;
import com.github.runicrebirth.entities.spells.MagicShieldEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@OnlyIn(Dist.CLIENT)
public class MagicShieldRenderer extends AbstractSpellRenderer<MagicShieldEntity> {

    private static final ResourceLocation FP_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/entity/magic_shield/fire_magic_shield_texture_fp.png");

    public MagicShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicShieldGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(MagicShieldEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON
                && mc.player != null
                && mc.player.getId() == entity.getOwnerId()) {

//            if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info(
//                String.format("[RunicRebirth] Rendering shield for first person"));
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
              return ResourceLocation.fromNamespaceAndPath(
                  RunicRebirth.MODID,
                  "textures/entity/magic_shield/" + parsed.getPath() + "_magic_shield_texture_fp.png"
              );
            }
            return FP_TEXTURE;
        }
        return super.getTextureLocation(entity);
    }

    @Override
    public RenderType getRenderType(MagicShieldEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, MagicShieldEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        float scale = entity.getShieldSize();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }
}
