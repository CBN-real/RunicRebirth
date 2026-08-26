package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.AdeptStatueBlockEntity;
import com.github.runicrebirth.blocks.entity.CrumblingPlatformBlockEntity;
import com.github.runicrebirth.blocks.entity.MeditationCushionBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonBoulderSpawnerBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonDoorBlockEntity;
import com.github.runicrebirth.blocks.entity.AncientArcaneTurretBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonFlamethrowerBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonSwingingAxeBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonPressurePlateBlockEntity;
import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.github.runicrebirth.blocks.entity.RunelightLanternBlockEntity;
import com.github.runicrebirth.blocks.entity.RunelightTorchBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPillarBlockEntity;
import com.github.runicrebirth.blocks.entity.RunesteelPortcullisBlockEntity;
import com.github.runicrebirth.blocks.entity.RunesteelPylonBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.github.runicrebirth.blocks.entity.RunesteelCacheBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import com.github.runicrebirth.blocks.entity.DungeonRoomTrackerBlockEntity;
import com.github.runicrebirth.blocks.entity.RunicLeverBlockEntity;
import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import com.github.runicrebirth.blocks.entity.TatteredSectBannerBlockEntity;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonMobSpawnerBlockEntity>> DUNGEON_MOB_SPAWNER =
            BLOCK_ENTITIES.register("dungeon_mob_spawner",
                    () -> BlockEntityType.Builder.of(DungeonMobSpawnerBlockEntity::new, ModBlocks.DUNGEON_MOB_SPAWNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonRoomTrackerBlockEntity>> DUNGEON_ROOM_TRACKER =
            BLOCK_ENTITIES.register("dungeon_room_tracker",
                    () -> BlockEntityType.Builder.of(DungeonRoomTrackerBlockEntity::new, ModBlocks.DUNGEON_ROOM_TRACKER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusionAltarBlockEntity>> INFUSION_ALTAR =
            BLOCK_ENTITIES.register("infusion_altar",
                    () -> BlockEntityType.Builder.of(InfusionAltarBlockEntity::new, ModBlocks.INFUSION_ALTAR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicAnvilBlockEntity>> RUNIC_ANVIL =
            BLOCK_ENTITIES.register("runic_anvil",
                    () -> BlockEntityType.Builder.of(RunicAnvilBlockEntity::new, ModBlocks.RUNIC_ANVIL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelCacheBlockEntity>> RUNESTEEL_CACHE =
            BLOCK_ENTITIES.register("runesteel_cache",
                    () -> BlockEntityType.Builder.of(RunesteelCacheBlockEntity::new, ModBlocks.RUNESTEEL_CACHE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelPortcullisBlockEntity>> RUNESTEEL_PORTCULLIS =
            BLOCK_ENTITIES.register("runesteel_portcullis",
                    () -> BlockEntityType.Builder.of(RunesteelPortcullisBlockEntity::new,
                            ModBlocks.RUNESTEEL_PORTCULLIS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonDoorBlockEntity>> DUNGEON_DOOR =
            BLOCK_ENTITIES.register("dungeon_door",
                    () -> BlockEntityType.Builder.of(DungeonDoorBlockEntity::new,
                            ModBlocks.DUNGEON_DOOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunelightTorchBlockEntity>> RUNELIGHT_TORCH =
            BLOCK_ENTITIES.register("runelight_torch",
                    () -> BlockEntityType.Builder.of(RunelightTorchBlockEntity::new,
                            ModBlocks.RUNELIGHT_TORCH.get(), ModBlocks.RUNELIGHT_WALL_TORCH.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunelightLanternBlockEntity>> RUNELIGHT_LANTERN =
            BLOCK_ENTITIES.register("runelight_lantern",
                    () -> BlockEntityType.Builder.of(RunelightLanternBlockEntity::new,
                            ModBlocks.RUNELIGHT_LANTERN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonPressurePlateBlockEntity>> DUNGEON_PRESSURE_PLATE =
            BLOCK_ENTITIES.register("dungeon_pressure_plate",
                    () -> BlockEntityType.Builder.of(DungeonPressurePlateBlockEntity::new,
                            ModBlocks.DUNGEON_PRESSURE_PLATE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrumblingPlatformBlockEntity>> CRUMBLING_PLATFORM =
            BLOCK_ENTITIES.register("crumbling_platform",
                    () -> BlockEntityType.Builder.of(CrumblingPlatformBlockEntity::new,
                            ModBlocks.CRUMBLING_PLATFORM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonBoulderSpawnerBlockEntity>> DUNGEON_BOULDER_SPAWNER =
            BLOCK_ENTITIES.register("dungeon_boulder_spawner",
                    () -> BlockEntityType.Builder.of(DungeonBoulderSpawnerBlockEntity::new,
                            ModBlocks.DUNGEON_BOULDER_SPAWNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonSwingingAxeBlockEntity>> DUNGEON_SWINGING_AXE =
            BLOCK_ENTITIES.register("dungeon_swinging_axe",
                    () -> BlockEntityType.Builder.of(DungeonSwingingAxeBlockEntity::new,
                            ModBlocks.DUNGEON_SWINGING_AXE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonFlamethrowerBlockEntity>> DUNGEON_FLAMETHROWER =
            BLOCK_ENTITIES.register("dungeon_flamethrower",
                    () -> BlockEntityType.Builder.of(DungeonFlamethrowerBlockEntity::new,
                            ModBlocks.DUNGEON_FLAMETHROWER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientArcaneTurretBlockEntity>> ANCIENT_ARCANE_TURRET =
            BLOCK_ENTITIES.register("ancient_arcane_turret",
                    () -> BlockEntityType.Builder.of(AncientArcaneTurretBlockEntity::new,
                            ModBlocks.ANCIENT_ARCANE_TURRET.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicLeverBlockEntity>> RUNIC_LEVER =
            BLOCK_ENTITIES.register("runic_lever",
                    () -> BlockEntityType.Builder.of(RunicLeverBlockEntity::new, ModBlocks.RUNIC_LEVER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SectBannerBlockEntity>> SECT_BANNER =
            BLOCK_ENTITIES.register("sect_banner",
                    () -> BlockEntityType.Builder.of(SectBannerBlockEntity::new, ModBlocks.SECT_BANNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TatteredSectBannerBlockEntity>> TATTERED_SECT_BANNER =
            BLOCK_ENTITIES.register("tattered_sect_banner",
                    () -> BlockEntityType.Builder.of(TatteredSectBannerBlockEntity::new, ModBlocks.TATTERED_SECT_BANNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SectBannerVariantBlockEntity>> SECT_BANNER_VARIANT =
            BLOCK_ENTITIES.register("sect_banner_variant",
                    () -> BlockEntityType.Builder.of(SectBannerVariantBlockEntity::new,
                            ModBlocks.SECT_BANNER_MAGE.get(),
                            ModBlocks.SECT_BANNER_ARTIFICER.get(),
                            ModBlocks.SECT_BANNER_WIZARD.get(),
                            ModBlocks.SECT_BANNER_RUNEBLADE.get(),
                            ModBlocks.TATTERED_SECT_BANNER_MAGE.get(),
                            ModBlocks.TATTERED_SECT_BANNER_ARTIFICER.get(),
                            ModBlocks.TATTERED_SECT_BANNER_WIZARD.get(),
                            ModBlocks.TATTERED_SECT_BANNER_RUNEBLADE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeditationCushionBlockEntity>> MEDITATION_CUSHION =
            BLOCK_ENTITIES.register("meditation_cushion",
                    () -> BlockEntityType.Builder.of(MeditationCushionBlockEntity::new,
                            ModBlocks.MEDITATION_CUSHION.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AdeptStatueBlockEntity>> ADEPT_STATUE =
            BLOCK_ENTITIES.register("adept_statue",
                    () -> BlockEntityType.Builder.of(AdeptStatueBlockEntity::new,
                            ModBlocks.ADEPT_MAGE_STATUE.get(),
                            ModBlocks.ADEPT_WIZARD_STATUE.get(),
                            ModBlocks.ADEPT_RUNEBLADE_STATUE.get(),
                            ModBlocks.ADEPT_ARTIFICER_STATUE.get()).build(null));

    private ModBlockEntities() {}
}
