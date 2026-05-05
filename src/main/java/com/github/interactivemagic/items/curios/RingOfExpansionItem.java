package com.github.interactivemagic.items.curios;

import com.github.interactivemagic.api.item.ISpellEmpowerment;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.config.ServerConfig;
import com.github.interactivemagic.items.MagicItem;
import com.github.interactivemagic.spells.modifiers.AdditiveSizeModifier;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * Curios-equipped ring. Contributes an additive size modifier to every cast.
 * Delta configurable via ServerConfig.RING_OF_EXPANSION_DELTA.
 */
public class RingOfExpansionItem extends MagicItem implements ISpellEmpowerment {

    public RingOfExpansionItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public List<SpellModifier> contribute(ItemStack accessoryStack, SpellCastContext ctx) {
        int delta = ServerConfig.RING_OF_EXPANSION_DELTA.get();
        return List.of(new AdditiveSizeModifier(delta));
    }
}
