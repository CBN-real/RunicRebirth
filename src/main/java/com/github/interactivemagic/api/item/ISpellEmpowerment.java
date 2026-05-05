package com.github.interactivemagic.api.item;

import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellModifier;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public interface ISpellEmpowerment {
    List<SpellModifier> contribute(ItemStack accessoryStack, SpellCastContext ctx);
}
