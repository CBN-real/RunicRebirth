package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.ScaledSpellEntity;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCircleRenderer<T extends AbstractCircleEntity> extends GeoEntityRenderer<T> {

    protected AbstractCircleRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0f;
    }

    @Override
    public void preRender(PoseStack poseStack, T entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        if (!entity.isAddedToLevel()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            float ox = BookDisplayState.getOffsetX();
            float oy = BookDisplayState.getOffsetY();
            float oz = BookDisplayState.getOffsetZ();
            if (ox != 0f || oy != 0f || oz != 0f) {
                poseStack.translate(ox, oy, oz);
            }
        }
        if (entity instanceof AbstractCircleEntity circle) {
            float s = circle.getCircleScale();
            SpellType spellType = SpellTypeRegistry.get(
                ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, circle.getSpellTypeId()));
            if (spellType != null) {
                s = Math.max(1.0f, spellType.spellHeight() * s);
            }
            if (s != 1f) poseStack.scale(s, s, s);
        }
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (!entity.isAddedToLevel()) {
            return ModRenderTypes.entityUnlit(texture);
        }
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);
        boolean inBook = !animatable.isAddedToLevel();

        if (inBook) {
            RenderType unlitType = ModRenderTypes.entityUnlit(texture);
            VertexConsumer unlitBuffer = bufferSource.getBuffer(unlitType);
            super.renderRecursively(poseStack, animatable, bone, unlitType, bufferSource, unlitBuffer,
                isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
            return;
        }

        if (bone.getName().startsWith("rune_")) {
            RenderType runeType = RenderType.entityTranslucent(texture);
            VertexConsumer runeBuffer = bufferSource.getBuffer(runeType);
            super.renderRecursively(poseStack, animatable, bone, runeType, bufferSource, runeBuffer,
                isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
            return;
        }

        RenderType cutType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
        VertexConsumer cutBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(cutType));
        super.renderRecursively(poseStack, animatable, bone, cutType, bufferSource, cutBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }

    @Override
    protected void applyRotations(T entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }
}
