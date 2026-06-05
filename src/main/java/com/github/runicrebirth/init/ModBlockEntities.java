package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPillarBlockEntity;
import com.github.runicrebirth.blocks.entity.RunesteelPylonBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import com.github.runicrebirth.blocks.entity.TrialSpawnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RunicRebirth.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OculusPortalBlockEntity>> OCULUS_PORTAL =
            BLOCK_ENTITIES.register("oculus_portal",
                    () -> BlockEntityType.Builder.of(OculusPortalBlockEntity::new, ModBlocks.OCULUS_PORTAL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OculusControllerBlockEntity>> OCULUS_CONTROLLER =
            BLOCK_ENTITIES.register("oculus_controller",
                    () -> BlockEntityType.Builder.of(OculusControllerBlockEntity::new, ModBlocks.OCULUS_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OculusPillarBlockEntity>> OCULUS_PILLAR =
            BLOCK_ENTITIES.register("oculus_pillar",
                    () -> BlockEntityType.Builder.of(OculusPillarBlockEntity::new, ModBlocks.OCULUS_PILLAR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelPylonBlockEntity>> RUNESTEEL_PYLON =
            BLOCK_ENTITIES.register("runesteel_pylon",
                    () -> BlockEntityType.Builder.of(RunesteelPylonBlockEntity::new, ModBlocks.RUNESTEEL_PYLON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrialSpawnerBlockEntity>> TRIAL_SPAWNER =
            BLOCK_ENTITIES.register("trial_spawner",
                    () -> BlockEntityType.Builder.of(TrialSpawnerBlockEntity::new, ModBlocks.TRIAL_SPAWNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusionAltarBlockEntity>> INFUSION_ALTAR =
            BLOCK_ENTITIES.register("infusion_altar",
                    () -> BlockEntityType.Builder.of(InfusionAltarBlockEntity::new, ModBlocks.INFUSION_ALTAR.get()).build(null));

    private ModBlockEntities() {}
}
