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
                    () -> new BlockEntityType<>(OculusPortalBlockEntity::new, ModBlocks.OCULUS_PORTAL.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OculusControllerBlockEntity>> OCULUS_CONTROLLER =
            BLOCK_ENTITIES.register("oculus_controller",
                    () -> new BlockEntityType<>(OculusControllerBlockEntity::new, ModBlocks.OCULUS_CONTROLLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OculusPillarBlockEntity>> OCULUS_PILLAR =
            BLOCK_ENTITIES.register("oculus_pillar",
                    () -> new BlockEntityType<>(OculusPillarBlockEntity::new, ModBlocks.OCULUS_PILLAR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelPylonBlockEntity>> RUNESTEEL_PYLON =
            BLOCK_ENTITIES.register("runesteel_pylon",
                    () -> new BlockEntityType<>(RunesteelPylonBlockEntity::new, ModBlocks.RUNESTEEL_PYLON.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonMobSpawnerBlockEntity>> DUNGEON_MOB_SPAWNER =
            BLOCK_ENTITIES.register("dungeon_mob_spawner",
                    () -> new BlockEntityType<>(DungeonMobSpawnerBlockEntity::new, ModBlocks.DUNGEON_MOB_SPAWNER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonRoomTrackerBlockEntity>> DUNGEON_ROOM_TRACKER =
            BLOCK_ENTITIES.register("dungeon_room_tracker",
                    () -> new BlockEntityType<>(DungeonRoomTrackerBlockEntity::new, ModBlocks.DUNGEON_ROOM_TRACKER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusionAltarBlockEntity>> INFUSION_ALTAR =
            BLOCK_ENTITIES.register("infusion_altar",
                    () -> new BlockEntityType<>(InfusionAltarBlockEntity::new, ModBlocks.INFUSION_ALTAR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicAnvilBlockEntity>> RUNIC_ANVIL =
            BLOCK_ENTITIES.register("runic_anvil",
                    () -> new BlockEntityType<>(RunicAnvilBlockEntity::new, ModBlocks.RUNIC_ANVIL.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelCacheBlockEntity>> RUNESTEEL_CACHE =
            BLOCK_ENTITIES.register("runesteel_cache",
                    () -> new BlockEntityType<>(RunesteelCacheBlockEntity::new, ModBlocks.RUNESTEEL_CACHE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunesteelPortcullisBlockEntity>> RUNESTEEL_PORTCULLIS =
            BLOCK_ENTITIES.register("runesteel_portcullis",
                    () -> new BlockEntityType<>(RunesteelPortcullisBlockEntity::new,
                            ModBlocks.RUNESTEEL_PORTCULLIS.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonDoorBlockEntity>> DUNGEON_DOOR =
            BLOCK_ENTITIES.register("dungeon_door",
                    () -> new BlockEntityType<>(DungeonDoorBlockEntity::new,
                            ModBlocks.DUNGEON_DOOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunelightTorchBlockEntity>> RUNELIGHT_TORCH =
            BLOCK_ENTITIES.register("runelight_torch",
                    () -> new BlockEntityType<>(RunelightTorchBlockEntity::new,
                            ModBlocks.RUNELIGHT_TORCH.get(), ModBlocks.RUNELIGHT_WALL_TORCH.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunelightLanternBlockEntity>> RUNELIGHT_LANTERN =
            BLOCK_ENTITIES.register("runelight_lantern",
                    () -> new BlockEntityType<>(RunelightLanternBlockEntity::new,
                            ModBlocks.RUNELIGHT_LANTERN.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonPressurePlateBlockEntity>> DUNGEON_PRESSURE_PLATE =
            BLOCK_ENTITIES.register("dungeon_pressure_plate",
                    () -> new BlockEntityType<>(DungeonPressurePlateBlockEntity::new,
                            ModBlocks.DUNGEON_PRESSURE_PLATE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrumblingPlatformBlockEntity>> CRUMBLING_PLATFORM =
            BLOCK_ENTITIES.register("crumbling_platform",
                    () -> new BlockEntityType<>(CrumblingPlatformBlockEntity::new,
                            ModBlocks.CRUMBLING_PLATFORM.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonBoulderSpawnerBlockEntity>> DUNGEON_BOULDER_SPAWNER =
            BLOCK_ENTITIES.register("dungeon_boulder_spawner",
                    () -> new BlockEntityType<>(DungeonBoulderSpawnerBlockEntity::new,
                            ModBlocks.DUNGEON_BOULDER_SPAWNER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonSwingingAxeBlockEntity>> DUNGEON_SWINGING_AXE =
            BLOCK_ENTITIES.register("dungeon_swinging_axe",
                    () -> new BlockEntityType<>(DungeonSwingingAxeBlockEntity::new,
                            ModBlocks.DUNGEON_SWINGING_AXE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonFlamethrowerBlockEntity>> DUNGEON_FLAMETHROWER =
            BLOCK_ENTITIES.register("dungeon_flamethrower",
                    () -> new BlockEntityType<>(DungeonFlamethrowerBlockEntity::new,
                            ModBlocks.DUNGEON_FLAMETHROWER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientArcaneTurretBlockEntity>> ANCIENT_ARCANE_TURRET =
            BLOCK_ENTITIES.register("ancient_arcane_turret",
                    () -> new BlockEntityType<>(AncientArcaneTurretBlockEntity::new,
                            ModBlocks.ANCIENT_ARCANE_TURRET.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicLeverBlockEntity>> RUNIC_LEVER =
            BLOCK_ENTITIES.register("runic_lever",
                    () -> new BlockEntityType<>(RunicLeverBlockEntity::new, ModBlocks.RUNIC_LEVER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SectBannerBlockEntity>> SECT_BANNER =
            BLOCK_ENTITIES.register("sect_banner",
                    () -> new BlockEntityType<>(SectBannerBlockEntity::new, ModBlocks.SECT_BANNER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TatteredSectBannerBlockEntity>> TATTERED_SECT_BANNER =
            BLOCK_ENTITIES.register("tattered_sect_banner",
                    () -> new BlockEntityType<>(TatteredSectBannerBlockEntity::new, ModBlocks.TATTERED_SECT_BANNER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SectBannerVariantBlockEntity>> SECT_BANNER_VARIANT =
            BLOCK_ENTITIES.register("sect_banner_variant",
                    () -> new BlockEntityType<>(SectBannerVariantBlockEntity::new,
                            ModBlocks.SECT_BANNER_MAGE.get(),
                            ModBlocks.SECT_BANNER_ARTIFICER.get(),
                            ModBlocks.SECT_BANNER_WIZARD.get(),
                            ModBlocks.SECT_BANNER_RUNEBLADE.get(),
                            ModBlocks.TATTERED_SECT_BANNER_MAGE.get(),
                            ModBlocks.TATTERED_SECT_BANNER_ARTIFICER.get(),
                            ModBlocks.TATTERED_SECT_BANNER_WIZARD.get(),
                            ModBlocks.TATTERED_SECT_BANNER_RUNEBLADE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeditationCushionBlockEntity>> MEDITATION_CUSHION =
            BLOCK_ENTITIES.register("meditation_cushion",
                    () -> new BlockEntityType<>(MeditationCushionBlockEntity::new,
                            ModBlocks.MEDITATION_CUSHION.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AdeptStatueBlockEntity>> ADEPT_STATUE =
            BLOCK_ENTITIES.register("adept_statue",
                    () -> new BlockEntityType<>(AdeptStatueBlockEntity::new,
                            ModBlocks.ADEPT_MAGE_STATUE.get(),
                            ModBlocks.ADEPT_WIZARD_STATUE.get(),
                            ModBlocks.ADEPT_RUNEBLADE_STATUE.get(),
                            ModBlocks.ADEPT_ARTIFICER_STATUE.get()));

    private ModBlockEntities() {}
}
