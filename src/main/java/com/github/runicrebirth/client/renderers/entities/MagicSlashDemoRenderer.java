package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicSlashDemoGeoModel;
import com.github.runicrebirth.entities.spells.demo.SpellDemoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicSlashDemoRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<SpellDemoEntity, R> {

    public MagicSlashDemoRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicSlashDemoGeoModel());
    }
}
