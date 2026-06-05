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

    public static void init() {}

    private ModSounds() {}
}
