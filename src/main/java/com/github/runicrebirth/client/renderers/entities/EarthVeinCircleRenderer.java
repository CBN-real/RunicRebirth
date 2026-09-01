package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.EarthVeinCircleGeoModel;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class EarthVeinCircleRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<EarthVeinCircleEntity, R> {

    public EarthVeinCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new EarthVeinCircleGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
      return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
