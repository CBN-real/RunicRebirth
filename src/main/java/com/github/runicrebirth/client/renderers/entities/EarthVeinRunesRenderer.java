package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.EarthVeinRunesGeoModel;
import com.github.runicrebirth.entities.EarthVeinRunesEntity;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class EarthVeinRunesRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<EarthVeinRunesEntity, R> {

    public EarthVeinRunesRenderer(EntityRendererProvider.Context context) {
        super(context, new EarthVeinRunesGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
      return RenderTypes.entityTranslucent(texture);
    }
}
