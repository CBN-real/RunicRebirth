package com.github.interactivemagic.items;

/**
 * Concrete inscribed wand. Spell stored in INSCRIBED_SPELL DataComponent.
 * Right-click casts. No canvas, no SpellStack interaction.
 */
public class InscribedWandItem extends InscribedTool {

    public InscribedWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
