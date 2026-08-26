package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.client.sounds.SoundManager;
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

    public static final DeferredHolder<SoundEvent, SoundEvent> INFUSION_QUICK_INFUSION =
        SOUNDS.register("infusion.quick_infusion", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion.quick_infusion")));

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

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_LONGSWORD =
        SOUNDS.register("spells.longsword", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.longsword")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_SPAWN_CIRCLE =
        SOUNDS.register("spells.spawn_circle", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.spawn_circle")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_ENERGY_CRACKLING =
        SOUNDS.register("spells.energy_crackling", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.energy_crackling")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_INITIATE_BINDING =
        SOUNDS.register("spells.initiate_binding", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.initiate_binding")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_HOLD_BINDING =
        SOUNDS.register("spells.hold_binding", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.hold_binding")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_END_BINDING =
        SOUNDS.register("spells.end_binding", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.end_binding")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_CHIME2 =
        SOUNDS.register("spells.chime2", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.chime2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_HAMMER_INITIATE =
        SOUNDS.register("spells.hammer_initiate", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.hammer_initiate")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_METEOR_CIRCLE_INITIATE =
        SOUNDS.register("spells.meteor_circle_initiate", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.meteor_circle_initiate")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_METEOR_INITIATE =
        SOUNDS.register("spells.meteor_initiate", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.meteor_initiate")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_METEOR_END =
        SOUNDS.register("spells.meteor_end", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.meteor_end")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_DOOR =
        SOUNDS.register("dungeon.door", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.door")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_DOOR_CLOSING =
        SOUNDS.register("dungeon.door_closing", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.door_closing")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_PRESSURE_PLATE =
        SOUNDS.register("dungeon.pressure_plate", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.pressure_plate")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_SPIKE =
        SOUNDS.register("dungeon.spike", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.spike")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_TEMPORARY_PLATFORM =
        SOUNDS.register("dungeon.temporary_platform", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.temporary_platform")));

  public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_CRUMBLING_PLATFORM =
      SOUNDS.register("dungeon.crumbling_platform", () -> SoundEvent.createVariableRangeEvent(
          ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.crumbling_platform")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUNGEON_TURRET_POWER_ON =
        SOUNDS.register("dungeon.turret_power_on", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon.turret_power_on")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_HOVER =
        SOUNDS.register("spells.hover", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.hover")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_THRUSTER =
        SOUNDS.register("spells.thruster", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.thruster")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_BLINK =
        SOUNDS.register("spells.blink", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.blink")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_LEAPING =
        SOUNDS.register("spells.leaping", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.leaping")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELLS_TETHER =
        SOUNDS.register("spells.tether", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spells.tether")));

    public static void init() {}

    private ModSounds() {}
}
