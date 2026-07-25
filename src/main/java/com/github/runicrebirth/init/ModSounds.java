package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(Registries.SOUND_EVENT, RunicRebirth.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CANVAS_SUCCESS =
        SOUNDS.register("canvas.success", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "canvas.success")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CANVAS_FAILED =
        SOUNDS.register("canvas.failed", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "canvas.failed")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CANVAS_AMBIENT =
        SOUNDS.register("canvas.canvas_ambient", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "canvas.canvas_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> OCULUS_CONTROLLER_OPEN =
        SOUNDS.register("oculus.controller_open", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "oculus.controller_open")));

    public static final DeferredHolder<SoundEvent, SoundEvent> OCULUS_PORTAL_OPEN =
        SOUNDS.register("oculus.portal_open", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "oculus.portal_open")));

    public static final DeferredHolder<SoundEvent, SoundEvent> INFUSION_ALTAR_ACTIVATE =
        SOUNDS.register("infusion.altar_activating", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion.altar_activating")));

    public static final DeferredHolder<SoundEvent, SoundEvent> INFUSION_ADD_ITEM =
        SOUNDS.register("infusion.add_item", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion.add_item")));

    public static final DeferredHolder<SoundEvent, SoundEvent> INFUSION_INFUSING =
        SOUNDS.register("infusion.infusing", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion.infusing")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FORGE_FORGING =
        SOUNDS.register("forging.forging", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "forging.forging")));

    public static final DeferredHolder<SoundEvent, SoundEvent> FORGE_ACTIVE =
        SOUNDS.register("forging.forge_active", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "forging.forge_active")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_EXPLOSION =
        SOUNDS.register("spells.explosion", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.explosion")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_FUSE =
        SOUNDS.register("spells.fuse", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.fuse")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_HAMMER_SPELL =
        SOUNDS.register("spells.hammer_spell", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.hammer_spell")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_LASER_SHOT =
        SOUNDS.register("spells.laser_shot", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.laser_shot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_LOAD_ARROW =
        SOUNDS.register("spells.load_arrow", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.load_arrow")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SHOOT_ARROW =
        SOUNDS.register("spells.shoot_arrow", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.shoot_arrow")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_LOAD_BALLISTA =
        SOUNDS.register("spells.load_ballista", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.load_ballista")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SHOOT_BALLISTA =
        SOUNDS.register("spells.shoot_ballista", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.shoot_ballista")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_PROJECTILE_SHOOT =
        SOUNDS.register("spells.projectile_shoot", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.projectile_shoot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SHIELD_INITIATE =
        SOUNDS.register("spells.shield_initiate", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.shield_initiate")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SHIELD_HIT =
        SOUNDS.register("spells.shield_hit", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.shield_hit")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SHIELD_END =
        SOUNDS.register("spells.shield_end", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.shield_end")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SLASH_SPELL =
        SOUNDS.register("spells.slash_spell", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.slash_spell")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SPAWN_CIRCLE =
        SOUNDS.register("spells.spawn_circle", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.spawn_circle")));

    public static void init() {}

    private ModSounds() {}
}
