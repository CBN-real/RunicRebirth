package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.AncientArcaneDroneEntity;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class AncientArcaneDroneGeoModel extends GeoModel<AncientArcaneDroneEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/arcane_drone.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/arcane_drone.animation.json");

    @Override
    public ResourceLocation getModelResource(AncientArcaneDroneEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(AncientArcaneDroneEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(AncientArcaneDroneEntity entity) { return ANIMATIONS; }

    @Override
    public void setCustomAnimations(AncientArcaneDroneEntity entity, long instanceId,
        AnimationState<AncientArcaneDroneEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      float partialTick = animationState.getPartialTick();
      GeoBone head = this.getAnimationProcessor().getBone("arcane_drone");
      if (head != null) {
        head.setRotX(Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot()) * Mth.DEG_TO_RAD);
        head.setRotY(Mth.lerp(partialTick,
            Mth.wrapDegrees(-entity.yHeadRotO + entity.yBodyRotO),
            Mth.wrapDegrees(-entity.yHeadRot + entity.yBodyRot)) * Mth.DEG_TO_RAD);
      }
    }
}
