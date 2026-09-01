package com.github.runicrebirth.client.renderers.armor;

import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.entity.EquipmentSlot;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.specialty.DyeableGeoArmorRenderer;

import java.util.EnumMap;

public class DyeableMagicArmorRenderer<T extends MagicArmorItem, R extends HumanoidRenderState> extends DyeableGeoArmorRenderer<T, R> {

    private final int defaultColor;

    public DyeableMagicArmorRenderer(GeoModel<T> model, int defaultColor) {
        super(model);
        this.defaultColor = defaultColor;
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return bone.name().startsWith("dye_");
    }

    @Override
    protected int getColorForBone(R renderState, GeoBone bone, int baseColour) {
        EnumMap<EquipmentSlot, ItemStack> equipment = ((com.geckolib.renderer.base.GeoRenderState) renderState).getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, (EnumMap<EquipmentSlot, ItemStack>) null);
        if (equipment != null) {
            for (ItemStack stack : equipment.values()) {
                if (!stack.isEmpty() && stack.getItem() instanceof MagicArmorItem) {
                    return 0xFF000000 | DyedItemColor.getOrDefault(stack, defaultColor);
                }
            }
        }
        return 0xFF000000 | defaultColor;
    }
}
