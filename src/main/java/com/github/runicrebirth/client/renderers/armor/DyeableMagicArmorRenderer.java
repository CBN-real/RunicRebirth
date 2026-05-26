package com.github.runicrebirth.client.renderers.armor;

import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

@OnlyIn(Dist.CLIENT)
public class DyeableMagicArmorRenderer<T extends MagicArmorItem> extends DyeableGeoArmorRenderer<T> {

    private final int defaultColor;

    public DyeableMagicArmorRenderer(GeoModel<T> model, int defaultColor) {
        super(model);
        this.defaultColor = defaultColor;
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return bone.getName().startsWith("dye");
    }

    @NotNull
    @Override
    protected Color getColorForBone(GeoBone bone) {
        if (getCurrentStack() != null) {
            return Color.ofOpaque(DyedItemColor.getOrDefault(getCurrentStack(), defaultColor));
        }
        return Color.WHITE;
    }
}
