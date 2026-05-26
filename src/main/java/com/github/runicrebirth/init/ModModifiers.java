package com.github.runicrebirth.init;

import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.spells.modifiers.*;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModModifiers {

    public static final DeferredHolder<SpellModifier, AdditiveSizeModifier> PLUS_SIZE =
        ModifierRegistry.MODIFIERS.register("additive_size",
            () -> new AdditiveSizeModifier(() -> ServerConfig.ADDITIVE_SIZE_DELTA.get()));

    public static final DeferredHolder<SpellModifier, SizeMultiplierModifier> SIZE_PLUS =
        ModifierRegistry.MODIFIERS.register("size_plus",
            () -> new SizeMultiplierModifier(SizeMultiplierModifier.ID_PLUS, "plus", 1.5f));

    public static final DeferredHolder<SpellModifier, SizeMultiplierModifier> SIZE_PLUS_TWO =
        ModifierRegistry.MODIFIERS.register("size_plus_two",
            () -> new SizeMultiplierModifier(SizeMultiplierModifier.ID_PLUS_TWO, "plus_two", 2.0f));

    public static final DeferredHolder<SpellModifier, SizeMultiplierModifier> SIZE_PLUS_FOUR =
        ModifierRegistry.MODIFIERS.register("size_plus_four",
            () -> new SizeMultiplierModifier(SizeMultiplierModifier.ID_PLUS_FOUR, "plus_four", 3.0f));

    public static final DeferredHolder<SpellModifier, RangeModifier> RANGE =
        ModifierRegistry.MODIFIERS.register("range", RangeModifier::new);

    public static final DeferredHolder<SpellModifier, CooldownModifier> COOLDOWN =
        ModifierRegistry.MODIFIERS.register("cooldown", CooldownModifier::new);

    public static final DeferredHolder<SpellModifier, MultiCastModifier> TWO_CASTS =
        ModifierRegistry.MODIFIERS.register("two_casts",
            () -> new MultiCastModifier(MultiCastModifier.ID_TWO, "two_casts", 2));

    public static final DeferredHolder<SpellModifier, MultiCastModifier> FOUR_CASTS =
        ModifierRegistry.MODIFIERS.register("four_casts",
            () -> new MultiCastModifier(MultiCastModifier.ID_FOUR, "four_casts", 4));

    public static final DeferredHolder<SpellModifier, ChargesModifier> CHARGES =
        ModifierRegistry.MODIFIERS.register("charges", ChargesModifier::new);

    private ModModifiers() {}

    public static void init() {}
}
