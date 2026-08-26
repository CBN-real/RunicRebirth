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

    public static final DeferredHolder<CriterionTrigger<?>, SpellDrawnTrigger> SPELL_DRAWN =
        TRIGGERS.register("spell_drawn", SpellDrawnTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, SpellCastTrigger> SPELL_CAST =
        TRIGGERS.register("spell_cast", SpellCastTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, ModifierKillTrigger> MODIFIER_KILL =
        TRIGGERS.register("modifier_kill", ModifierKillTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, WeaponKillTrigger> WEAPON_KILL =
        TRIGGERS.register("weapon_kill", WeaponKillTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, WeaponActiveUsedTrigger> WEAPON_ACTIVE_USED =
        TRIGGERS.register("weapon_active_used", WeaponActiveUsedTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, RingActivatedTrigger> RING_ACTIVATED =
        TRIGGERS.register("ring_activated", RingActivatedTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, DroneKillTrigger> DRONE_KILL =
        TRIGGERS.register("drone_kill", DroneKillTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, DungeonTrialClearedTrigger> DUNGEON_TRIAL_CLEARED =
        TRIGGERS.register("dungeon_trial_cleared", DungeonTrialClearedTrigger::new);

    private ModCriteriaTriggers() {}

    public static void init() {}
}
