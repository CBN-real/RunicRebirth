package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicSlashDemoGeoModel;
import com.github.runicrebirth.entities.spells.demo.SpellDemoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagicSlashDemoRenderer extends AbstractSpellRenderer<SpellDemoEntity> {

    public MagicSlashDemoRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicSlashDemoGeoModel());
    }
}
