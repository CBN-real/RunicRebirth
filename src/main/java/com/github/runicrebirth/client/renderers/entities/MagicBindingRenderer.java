package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicBindingGeoModel;
import com.github.runicrebirth.entities.spells.MagicBindingEntity;
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

public class MagicBindingRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicBindingEntity, R> {

    public MagicBindingRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBindingGeoModel());
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

}
