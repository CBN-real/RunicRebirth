package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public final class ModArmorMaterials {

    public static final ResourceKey<EquipmentAsset> ACOLYTE_ASSET =
        ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "acolyte"));
    public static final ResourceKey<EquipmentAsset> ADEPT_ASSET =
        ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "adept"));
    public static final ResourceKey<EquipmentAsset> ADEPT_RUNEBLADE_ASSET =
        ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "adept_runeblade"));

    public static final ArmorMaterial ACOLYTE = new ArmorMaterial(
        13,
        Map.of(ArmorType.HELMET, 1, ArmorType.CHESTPLATE, 3, ArmorType.LEGGINGS, 2, ArmorType.BOOTS, 1),
        15, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f,
        ItemTags.REPAIRS_LEATHER_ARMOR, ACOLYTE_ASSET
    );

    public static final ArmorMaterial ADEPT = new ArmorMaterial(
        18,
        Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 8, ArmorType.LEGGINGS, 6, ArmorType.BOOTS, 3),
        10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2f, 0f,
        ItemTags.REPAIRS_DIAMOND_ARMOR, ADEPT_ASSET
    );

    public static final ArmorMaterial ADEPT_RUNEBLADE = new ArmorMaterial(
        18,
        Map.of(ArmorType.HELMET, 4, ArmorType.CHESTPLATE, 9, ArmorType.LEGGINGS, 7, ArmorType.BOOTS, 4),
        10, SoundEvents.ARMOR_EQUIP_DIAMOND, 3f, 0f,
        ItemTags.REPAIRS_DIAMOND_ARMOR, ADEPT_RUNEBLADE_ASSET
    );

    private ModArmorMaterials() {}
}
