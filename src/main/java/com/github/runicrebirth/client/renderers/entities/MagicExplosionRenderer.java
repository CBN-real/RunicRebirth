package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicExplosionGeoModel;
import com.github.runicrebirth.entities.spells.MagicExplosionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicExplosionRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicExplosionEntity, R> {

    public MagicExplosionRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicExplosionGeoModel());
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

}
