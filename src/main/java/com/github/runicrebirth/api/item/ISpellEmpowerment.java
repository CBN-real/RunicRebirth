package com.github.runicrebirth.api.item;

import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellModifier;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public interface ISpellEmpowerment {
    List<SpellModifier> contribute(ItemStack accessoryStack, SpellCastContext ctx);
}
