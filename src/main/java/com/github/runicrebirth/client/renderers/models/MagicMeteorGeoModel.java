package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.MagicMeteorEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.github.runicrebirth.client.renderers.entities.AbstractSpellRenderer;

public class MagicMeteorGeoModel extends GeoModel<MagicMeteorEntity> {

    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell/magic_meteor");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell/magic_meteor");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        String elementId = renderState.getGeckolibData(AbstractSpellRenderer.SPELL_ELEMENT_ID);
        if (elementId != null) {
            net.minecraft.resources.Identifier parsed = net.minecraft.resources.Identifier.tryParse(elementId);
            if (parsed != null) {
                return net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/" + parsed.getPath() + "_runic_template.png");
            }
        }
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(MagicMeteorEntity animatable) {
        return ANIMATIONS;
    }
}
