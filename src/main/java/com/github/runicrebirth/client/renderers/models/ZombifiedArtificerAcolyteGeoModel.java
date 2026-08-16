package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class ZombifiedArtificerAcolyteGeoModel extends GeoModel<ZombifiedArtificerAcolyteEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/zombified_artificer_acolyte.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/zombified_acolyte.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/zombified_artificer_acolyte.animation.json");

    @Override
    public ResourceLocation getModelResource(ZombifiedArtificerAcolyteEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(ZombifiedArtificerAcolyteEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(ZombifiedArtificerAcolyteEntity entity) { return ANIMATIONS; }

    @Override
    public void setCustomAnimations(ZombifiedArtificerAcolyteEntity entity, long instanceId, AnimationState<ZombifiedArtificerAcolyteEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        float partialTick = animationState.getPartialTick();
        GeoBone head = this.getAnimationProcessor().getBone("bipedHead");
        if (head != null) {
            head.setRotX(Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot()) * Mth.DEG_TO_RAD);
            head.setRotY(Mth.lerp(partialTick,
                Mth.wrapDegrees(-entity.yHeadRotO + entity.yBodyRotO),
                Mth.wrapDegrees(-entity.yHeadRot + entity.yBodyRot)) * Mth.DEG_TO_RAD);
        }
    }
}
