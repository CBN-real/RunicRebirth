package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicBallistaDemoGeoModel;
import com.github.runicrebirth.entities.spells.demo.SpellDemoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicBallistaDemoRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<SpellDemoEntity, R> {

    public MagicBallistaDemoRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBallistaDemoGeoModel());
    }
}
