package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicBallistaDemoGeoModel;
import com.github.runicrebirth.entities.spells.demo.SpellDemoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagicBallistaDemoRenderer extends AbstractSpellRenderer<SpellDemoEntity> {

    public MagicBallistaDemoRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBallistaDemoGeoModel());
    }
}
