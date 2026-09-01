package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.renderers.models.MagicShieldGeoModel;
import com.github.runicrebirth.entities.spells.MagicShieldEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicShieldRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicShieldEntity, R> {

    private static final DataTicket<Integer> SHIELD_OWNER_ID = DataTicket.create("runicrebirth:shield_owner_id", Integer.class);
    private static final DataTicket<String> SHIELD_ELEMENT_ID = DataTicket.create("runicrebirth:shield_element_id", String.class);
    private static final DataTicket<Float> SHIELD_SCALE = DataTicket.create("runicrebirth:shield_scale", Float.class);

    private static final Identifier FP_TEXTURE = Identifier.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/entity/magic_shield/fire_magic_shield_texture_fp.png");

    public MagicShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicShieldGeoModel());
    }

    @Override
    public void extractRenderState(MagicShieldEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(SHIELD_OWNER_ID, entity.getOwnerId());
        renderState.addGeckolibData(SHIELD_ELEMENT_ID, entity.getElementId());
        float size = entity.getProjectileSize();
        renderState.addGeckolibData(SHIELD_SCALE, size > 0 ? 1f / size : 1f);
    }

    @Override
    public Identifier getTextureLocation(R renderState) {
        Minecraft mc = Minecraft.getInstance();
        Integer ownerId = renderState.getGeckolibData(SHIELD_OWNER_ID);
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON
                && mc.player != null && ownerId != null
                && mc.player.getId() == ownerId) {
            String elementId = renderState.getOrDefaultGeckolibData(SHIELD_ELEMENT_ID, "");
            Identifier parsed = elementId != null ? Identifier.tryParse(elementId) : null;
            if (parsed != null) {
                return Identifier.fromNamespaceAndPath(
                    RunicRebirth.MODID,
                    "textures/entity/magic_shield/" + parsed.getPath() + "_magic_shield_texture_fp.png"
                );
            }
            return FP_TEXTURE;
        }
        return super.getTextureLocation(renderState);
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucentEmissive(texture);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(SHIELD_SCALE, 1f);
        renderPassInfo.poseStack().scale(scale, scale, scale);
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }
}
