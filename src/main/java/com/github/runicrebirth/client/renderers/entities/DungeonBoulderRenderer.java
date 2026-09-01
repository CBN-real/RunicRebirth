package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.DungeonBoulderGeoModel;
import com.github.runicrebirth.entities.DungeonBoulderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;
import org.jspecify.annotations.Nullable;

public class DungeonBoulderRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<DungeonBoulderEntity, R> {

    private static final DataTicket<Direction> TRAVEL_DIR_TICKET = DataTicket.create("runicrebirth:boulder_travel_dir", Direction.class);

    public DungeonBoulderRenderer(EntityRendererProvider.Context context) {
        super(context, new DungeonBoulderGeoModel());
        this.shadowRadius = 1.5f;
    }

    @Override
    public void extractRenderState(DungeonBoulderEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(TRAVEL_DIR_TICKET, entity.getTravelDirection());
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        Direction dir = renderPassInfo.getOrDefaultGeckolibData(TRAVEL_DIR_TICKET, Direction.NORTH);
        poseStack.mulPose(Axis.YP.rotationDegrees(dir.get2DDataValue() * 90f));
    }
}
