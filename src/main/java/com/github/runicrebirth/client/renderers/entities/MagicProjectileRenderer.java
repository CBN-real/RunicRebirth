package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicProjectileGeoModel;
import com.github.runicrebirth.entities.spells.MagicProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicProjectileRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicProjectileEntity, R> {

  public MagicProjectileRenderer(EntityRendererProvider.Context context) {
    super(context, new MagicProjectileGeoModel());
  }

}
