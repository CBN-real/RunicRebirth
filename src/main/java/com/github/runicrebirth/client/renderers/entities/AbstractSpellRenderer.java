package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.api.spells.ElementalEntity;
import com.github.runicrebirth.api.spells.ScaledSpellEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public abstract class AbstractSpellRenderer<T extends Entity & GeoEntity, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {

    public static final DataTicket<Float> SPELL_XROT = DataTicket.create("runicrebirth:spell_xrot", Float.class);
    public static final DataTicket<Float> SPELL_YROT = DataTicket.create("runicrebirth:spell_yrot", Float.class);
    public static final DataTicket<String> SPELL_ELEMENT_ID = DataTicket.create("runicrebirth:spell_element_id", String.class);

    protected AbstractSpellRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0f;
    }

    @Override
    public void extractRenderState(T entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(SPELL_XROT, entity.getXRot());
        renderState.addGeckolibData(SPELL_YROT, entity.getYRot());
        if (entity instanceof ElementalEntity ee) {
            renderState.addGeckolibData(SPELL_ELEMENT_ID, ee.getElementId());
        }
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
