package com.github.interactivemagic.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IInscribedSpell {
    @Nullable
    ResourceLocation getSpell(ItemStack stack);

    void setSpell(ItemStack stack, @Nullable ResourceLocation spell);
}
