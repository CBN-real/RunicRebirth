package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AncientArcaneTurretBlock;
import com.github.runicrebirth.blocks.entity.AncientArcaneTurretBlockEntity;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class AncientArcaneTurretRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<AncientArcaneTurretBlockEntity, R> {

    public AncientArcaneTurretRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AncientArcaneTurretModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        BlockState state = Minecraft.getInstance().level.getBlockState(renderPassInfo.renderState().blockPos);
        AttachFace face = state.getValue(AncientArcaneTurretBlock.FACE);

        if (face != AttachFace.WALL) return;

        Direction facing = state.getValue(AncientArcaneTurretBlock.FACING);
        float yDeg = switch (facing) {
            case NORTH -> 180f;
            case EAST  -> 180f;
            case WEST  -> 180f;
            default    -> 180f; // SOUTH = base wall model orientation
        };

        renderPassInfo.poseStack().mulPose(Axis.YP.rotationDegrees(yDeg));
    }
}
