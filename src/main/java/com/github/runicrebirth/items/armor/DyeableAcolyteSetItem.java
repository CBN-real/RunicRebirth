package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;

import java.util.List;

public class DyeableAcolyteSetItem extends DyeableMagicArmorItem {

    public static final int DEFAULT_DYE_COLOR = 0xFFA3A4A5;

    public DyeableAcolyteSetItem(Properties props, String armorName, List<SpellModifier> modifiers) {
        super(props, armorName, armorName, 0f, 0f, 0f, modifiers, DEFAULT_DYE_COLOR);
    }
}
