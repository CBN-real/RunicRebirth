package com.github.runicrebirth.client.renderers.blocks;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public abstract class AbstractRunicBlockRenderer<T extends BlockEntity & GeoBlockEntity, R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<T, R> {

    protected AbstractRunicBlockRenderer(BlockEntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }
}
