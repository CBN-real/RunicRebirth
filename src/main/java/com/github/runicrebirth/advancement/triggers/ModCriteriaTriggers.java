package com.github.runicrebirth.advancement.triggers;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCriteriaTriggers {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
        DeferredRegister.create(Registries.TRIGGER_TYPE, RunicRebirth.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, HeldSpellWriterTrigger> HELD_SPELL_WRITER =
        TRIGGERS.register("held_spell_writer", HeldSpellWriterTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, MagicKillTrigger> MAGIC_KILL =
        TRIGGERS.register("magic_kill", MagicKillTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, ElementTrialTrigger> ELEMENT_TRIAL =
        TRIGGERS.register("element_trial", ElementTrialTrigger::new);

    private ModCriteriaTriggers() {}

    public static void init() {}
}
