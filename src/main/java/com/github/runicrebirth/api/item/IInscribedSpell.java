package com.github.runicrebirth.api.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IInscribedSpell {
    @Nullable
    Identifier getSpell(ItemStack stack);

    void setSpell(ItemStack stack, @Nullable Identifier spell);
}
