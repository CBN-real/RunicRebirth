package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;

import java.util.List;

public class DyeableAdeptSetItem extends DyeableMagicArmorItem {

    private static final int DEFAULT_DYE_COLOR = 0xFFA3A4A5;

    public DyeableAdeptSetItem(Properties props, String armorName, String textureName, List<SpellModifier> modifiers) {
        super(props, armorName, textureName, 0f, 0f, 0f, modifiers, DEFAULT_DYE_COLOR);
    }
}
