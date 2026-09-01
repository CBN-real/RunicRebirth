package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.RunesteelCacheBlock;
import com.github.runicrebirth.blocks.AdeptStatueBlock;
import com.github.runicrebirth.blocks.AdeptStatueProxyBlock;
import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.blocks.DungeonDoorProxyBlock;
import com.github.runicrebirth.blocks.CrumblingPlatformBlock;
import com.github.runicrebirth.blocks.DungeonPressurePlateBlock;
import com.github.runicrebirth.blocks.DungeonBoulderSpawnerBlock;
import com.github.runicrebirth.blocks.AncientArcaneTurretBlock;
import com.github.runicrebirth.blocks.DungeonFlamethrowerBlock;
import com.github.runicrebirth.blocks.DungeonSpikeBlock;
import com.github.runicrebirth.blocks.DungeonSwingingAxeBlock;
import com.github.runicrebirth.blocks.DungeonTemporaryPlatformBlock;
import com.github.runicrebirth.blocks.InfusionAltarBlock;
import com.github.runicrebirth.blocks.InfusionAltarProxyBlock;
import com.github.runicrebirth.blocks.OculusPillarBlock;
import com.github.runicrebirth.blocks.OculusPortalBlock;
import com.github.runicrebirth.blocks.ReturnPortalBlock;
import com.github.runicrebirth.blocks.RunelightLanternBlock;
import com.github.runicrebirth.blocks.RunelightTorchBlock;
import com.github.runicrebirth.blocks.RunelightWallTorchBlock;
import com.github.runicrebirth.blocks.RunesteelPortcullisBlock;
import com.github.runicrebirth.blocks.RunesteelPylonBlock;
import com.github.runicrebirth.blocks.OculusControllerBlock;
import com.github.runicrebirth.blocks.RunicAnvilBlock;
import com.github.runicrebirth.blocks.DungeonMobSpawnerBlock;
import com.github.runicrebirth.blocks.DungeonRoomTrackerBlock;
import com.github.runicrebirth.blocks.MeditationCushionBlock;
import com.github.runicrebirth.blocks.RunicLeverBlock;
import com.github.runicrebirth.blocks.SectBannerBlock;
import com.github.runicrebirth.blocks.TatteredSectBannerBlock;
import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(
        RunicRebirth.MODID);

    public static final DeferredBlock<Block> RUNIC_STONE = BLOCKS.registerSimpleBlock("runic_stone",
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredBlock<SlabBlock> RUNIC_STONE_SLAB = BLOCKS.registerBlock("runic_stone_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredBlock<StairBlock> RUNIC_STONE_STAIRS = BLOCKS.registerBlock("runic_stone_stairs",
            p -> new StairBlock(RUNIC_STONE.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredBlock<WallBlock> RUNIC_STONE_WALL = BLOCKS.registerBlock("runic_stone_wall",
            WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> RUNIC_STONE_PILLAR = BLOCKS.registerSimpleBlock("runic_stone_pillar",
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion());

    public static final DeferredBlock<Block> RUNIC_STONE_BRICKS = BLOCKS.registerSimpleBlock("runic_stone_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<SlabBlock> RUNIC_STONE_BRICKS_SLAB = BLOCKS.registerBlock("runic_stone_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<StairBlock> RUNIC_STONE_BRICKS_STAIRS = BLOCKS.registerBlock("runic_stone_bricks_stairs",
        p -> new StairBlock(RUNIC_STONE_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<WallBlock> RUNIC_STONE_BRICKS_WALL = BLOCKS.registerBlock("runic_stone_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> ARCANE_RUNIC_STONE_BRICKS = BLOCKS.registerSimpleBlock("arcane_runic_stone_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(s -> 4));

    public static final DeferredBlock<Block> CRACKED_RUNIC_STONE_BRICKS = BLOCKS.registerSimpleBlock("cracked_runic_stone_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<SlabBlock> CRACKED_RUNIC_STONE_BRICKS_SLAB = BLOCKS.registerBlock("cracked_runic_stone_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<StairBlock> CRACKED_RUNIC_STONE_BRICKS_STAIRS = BLOCKS.registerBlock("cracked_runic_stone_bricks_stairs",
        p -> new StairBlock(CRACKED_RUNIC_STONE_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<WallBlock> CRACKED_RUNIC_STONE_BRICKS_WALL = BLOCKS.registerBlock("cracked_runic_stone_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> FROZEN_RUNIC_BRICKS = BLOCKS.registerSimpleBlock("frozen_runic_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> FROZEN_RUNIC_BRICKS_SLAB = BLOCKS.registerBlock("frozen_runic_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<StairBlock> FROZEN_RUNIC_BRICKS_STAIRS = BLOCKS.registerBlock("frozen_runic_bricks_stairs",
        p -> new StairBlock(FROZEN_RUNIC_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<WallBlock> FROZEN_RUNIC_BRICKS_WALL = BLOCKS.registerBlock("frozen_runic_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> FLAMING_RUNIC_BRICKS = BLOCKS.registerSimpleBlock("flaming_runic_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> FLAMING_RUNIC_BRICKS_SLAB = BLOCKS.registerBlock("flaming_runic_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<StairBlock> FLAMING_RUNIC_BRICKS_STAIRS = BLOCKS.registerBlock("flaming_runic_bricks_stairs",
        p -> new StairBlock(FLAMING_RUNIC_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<WallBlock> FLAMING_RUNIC_BRICKS_WALL = BLOCKS.registerBlock("flaming_runic_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> EARTHEN_RUNIC_BRICKS = BLOCKS.registerSimpleBlock("earthen_runic_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> EARTHEN_RUNIC_BRICKS_SLAB = BLOCKS.registerBlock("earthen_runic_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<StairBlock> EARTHEN_RUNIC_BRICKS_STAIRS = BLOCKS.registerBlock("earthen_runic_bricks_stairs",
        p -> new StairBlock(EARTHEN_RUNIC_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<WallBlock> EARTHEN_RUNIC_BRICKS_WALL = BLOCKS.registerBlock("earthen_runic_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> WINDSWEPT_RUNIC_BRICKS = BLOCKS.registerSimpleBlock("windswept_runic_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> WINDSWEPT_RUNIC_BRICKS_SLAB = BLOCKS.registerBlock("windswept_runic_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<StairBlock> WINDSWEPT_RUNIC_BRICKS_STAIRS = BLOCKS.registerBlock("windswept_runic_bricks_stairs",
        p -> new StairBlock(WINDSWEPT_RUNIC_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<WallBlock> WINDSWEPT_RUNIC_BRICKS_WALL = BLOCKS.registerBlock("windswept_runic_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> MOSSY_RUNIC_BRICKS = BLOCKS.registerSimpleBlock("mossy_runic_bricks",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<SlabBlock> MOSSY_RUNIC_BRICKS_SLAB = BLOCKS.registerBlock("mossy_runic_bricks_slab",
        SlabBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<StairBlock> MOSSY_RUNIC_BRICKS_STAIRS = BLOCKS.registerBlock("mossy_runic_bricks_stairs",
        p -> new StairBlock(MOSSY_RUNIC_BRICKS.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));
    public static final DeferredBlock<WallBlock> MOSSY_RUNIC_BRICKS_WALL = BLOCKS.registerBlock("mossy_runic_bricks_wall",
        WallBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL));

    public static final DeferredBlock<Block> RUNESTEEL_BLOCK = BLOCKS.registerSimpleBlock("runesteel_block",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<Block> FLAMING_RUNESTEEL_BLOCK = BLOCKS.registerSimpleBlock("flaming_runesteel_block",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<Block> WINDSWEPT_RUNESTEEL_BLOCK = BLOCKS.registerSimpleBlock("windswept_runesteel_block",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<Block> FROZEN_RUNESTEEL_BLOCK = BLOCKS.registerSimpleBlock("frozen_runesteel_block",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<Block> EARTHEN_RUNESTEEL_BLOCK = BLOCKS.registerSimpleBlock("earthen_runesteel_block",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> FALSE_SKY = BLOCKS.registerSimpleBlock("false_sky",
        () -> Properties.of()
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .isValidSpawn((state, level, pos, entityType) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> true)
            .lightLevel(s -> 7));

    public static final DeferredBlock<Block> CRACKED_FALSE_SKY = BLOCKS.registerSimpleBlock("cracked_false_sky",
        () -> Properties.of()
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .isValidSpawn((state, level, pos, entityType) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> true)
            .lightLevel(s -> 7));

    public static final DeferredBlock<Block> CUT_RUNIC_STONE = BLOCKS.registerSimpleBlock("cut_runic_stone",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<Block> REINFORCED_CUT_RUNIC_STONE = BLOCKS.registerSimpleBlock("reinforced_cut_runic_stone",
        () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    public static final DeferredBlock<OculusPortalBlock> OCULUS_PORTAL = BLOCKS.registerBlock("oculus_portal",
            OculusPortalBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL).noOcclusion().lightLevel(s -> 12));

    public static final DeferredBlock<OculusControllerBlock> OCULUS_CONTROLLER = BLOCKS.registerBlock("oculus_controller",
            OculusControllerBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).noOcclusion());

    public static final DeferredBlock<OculusPillarBlock> OCULUS_PILLAR = BLOCKS.registerBlock("oculus_pillar",
            OculusPillarBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7));

    public static final DeferredBlock<RunesteelPylonBlock> RUNESTEEL_PYLON = BLOCKS.registerBlock("runesteel_pylon",
            RunesteelPylonBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7));

    public static final DeferredBlock<InfusionAltarBlock> INFUSION_ALTAR = BLOCKS.registerBlock("infusion_altar",
            InfusionAltarBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7));

    public static final DeferredBlock<InfusionAltarProxyBlock> INFUSION_ALTAR_PROXY = BLOCKS.registerBlock("infusion_altar_proxy",
            InfusionAltarProxyBlock::new, () -> BlockBehaviour.Properties.of().noCollision().noOcclusion().noLootTable().replaceable());

    public static final DeferredBlock<RunicAnvilBlock> RUNIC_ANVIL = BLOCKS.registerBlock("runic_anvil",
            RunicAnvilBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 5));

    public static final DeferredBlock<RunesteelCacheBlock> RUNESTEEL_CACHE = BLOCKS.registerBlock("runesteel_cache",
            RunesteelCacheBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 3));

    public static final DeferredBlock<RunesteelPortcullisBlock> RUNESTEEL_PORTCULLIS = BLOCKS.registerBlock("runesteel_portcullis",
            RunesteelPortcullisBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).noOcclusion());

    // Dungeon blocks
    public static final DeferredBlock<ReturnPortalBlock> RETURN_PORTAL = BLOCKS.registerBlock("return_portal",
            ReturnPortalBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 12));

    public static final DeferredBlock<DungeonMobSpawnerBlock> DUNGEON_MOB_SPAWNER = BLOCKS.registerBlock("dungeon_mob_spawner",
            DungeonMobSpawnerBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 5));

    public static final DeferredBlock<DungeonRoomTrackerBlock> DUNGEON_ROOM_TRACKER = BLOCKS.registerBlock("dungeon_room_tracker",
            DungeonRoomTrackerBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 3));

    public static final DeferredBlock<DungeonDoorBlock> DUNGEON_DOOR = BLOCKS.registerBlock("dungeon_door",
            DungeonDoorBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR).noOcclusion().strength(5.0f));

    public static final DeferredBlock<DungeonDoorProxyBlock> DUNGEON_DOOR_PROXY = BLOCKS.registerBlock("dungeon_door_proxy",
            DungeonDoorProxyBlock::new, () -> BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(5.0f, 3600000.0f));

    public static final DeferredBlock<RunelightTorchBlock> RUNELIGHT_TORCH = BLOCKS.registerBlock("runelight_torch",
            RunelightTorchBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).noOcclusion().lightLevel(s -> 15));

    public static final DeferredBlock<RunelightWallTorchBlock> RUNELIGHT_WALL_TORCH = BLOCKS.registerBlock("runelight_wall_torch",
            RunelightWallTorchBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).noOcclusion().lightLevel(s -> 15));

    public static final DeferredBlock<RunelightLanternBlock> RUNELIGHT_LANTERN = BLOCKS.registerBlock("runelight_lantern",
            RunelightLanternBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).noOcclusion().lightLevel(s -> 15).destroyTime(0.5f));

    // Dungeon trap blocks
    public static final DeferredBlock<DungeonTemporaryPlatformBlock> DUNGEON_TEMPORARY_PLATFORM = BLOCKS.registerBlock("dungeon_temporary_platform",
            DungeonTemporaryPlatformBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0f).noOcclusion());

    public static final DeferredBlock<DungeonPressurePlateBlock> DUNGEON_PRESSURE_PLATE = BLOCKS.registerBlock("dungeon_pressure_plate",
            DungeonPressurePlateBlock::new, () -> BlockBehaviour.Properties.of().noOcclusion().noCollision().strength(0.5f));

    public static final DeferredBlock<DungeonSpikeBlock> DUNGEON_SPIKE = BLOCKS.registerBlock("dungeon_spike",
            DungeonSpikeBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(2.0f));

    public static final DeferredBlock<CrumblingPlatformBlock> CRUMBLING_PLATFORM = BLOCKS.registerBlock("crumbling_platform",
            CrumblingPlatformBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(0.5f).noOcclusion());

    // Dungeon trap geo blocks
    public static final DeferredBlock<DungeonBoulderSpawnerBlock> DUNGEON_BOULDER_SPAWNER = BLOCKS.registerBlock("dungeon_boulder_spawner",
            DungeonBoulderSpawnerBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(4.0f));

    public static final DeferredBlock<DungeonSwingingAxeBlock> DUNGEON_SWINGING_AXE = BLOCKS.registerBlock("dungeon_swinging_axe",
            DungeonSwingingAxeBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(4.0f));

    public static final DeferredBlock<DungeonFlamethrowerBlock> DUNGEON_FLAMETHROWER = BLOCKS.registerBlock("dungeon_flamethrower",
            DungeonFlamethrowerBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(2.0f));

    public static final DeferredBlock<AncientArcaneTurretBlock> ANCIENT_ARCANE_TURRET = BLOCKS.registerBlock("ancient_arcane_turret",
            AncientArcaneTurretBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(3.0f).lightLevel(s -> 4));

    public static final DeferredBlock<MeditationCushionBlock> MEDITATION_CUSHION = BLOCKS.registerBlock("meditation_cushion",
            MeditationCushionBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).strength(0.5f).noOcclusion());

    public static final DeferredBlock<RunicLeverBlock> RUNIC_LEVER = BLOCKS.registerBlock("runic_lever",
            RunicLeverBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f));

    // --- Sect Banner blocks ---
    public static final DeferredBlock<SectBannerBlock> SECT_BANNER = BLOCKS.registerBlock("sect_banner",
            SectBannerBlock::new, () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<TatteredSectBannerBlock> TATTERED_SECT_BANNER = BLOCKS.registerBlock("tattered_sect_banner",
            TatteredSectBannerBlock::new, () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_MAGE = BLOCKS.registerBlock("sect_banner_mage",
            p -> new SectBannerVariantBlock(p, "sect_banner_mage", SectBannerVariantBlock.BannerType.SECT),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_ARTIFICER = BLOCKS.registerBlock("sect_banner_artificer",
            p -> new SectBannerVariantBlock(p, "sect_banner_artificer", SectBannerVariantBlock.BannerType.SECT),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_WIZARD = BLOCKS.registerBlock("sect_banner_wizard",
            p -> new SectBannerVariantBlock(p, "sect_banner_wizard", SectBannerVariantBlock.BannerType.SECT),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_RUNEBLADE = BLOCKS.registerBlock("sect_banner_runeblade",
            p -> new SectBannerVariantBlock(p, "sect_banner_runeblade", SectBannerVariantBlock.BannerType.SECT),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_MAGE = BLOCKS.registerBlock("tattered_sect_banner_mage",
            p -> new SectBannerVariantBlock(p, "tattered_sect_banner_mage", SectBannerVariantBlock.BannerType.TATTERED),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_ARTIFICER = BLOCKS.registerBlock("tattered_sect_banner_artificer",
            p -> new SectBannerVariantBlock(p, "tattered_sect_banner_artificer", SectBannerVariantBlock.BannerType.TATTERED),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_WIZARD = BLOCKS.registerBlock("tattered_sect_banner_wizard",
            p -> new SectBannerVariantBlock(p, "tattered_sect_banner_wizard", SectBannerVariantBlock.BannerType.TATTERED),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_RUNEBLADE = BLOCKS.registerBlock("tattered_sect_banner_runeblade",
            p -> new SectBannerVariantBlock(p, "tattered_sect_banner_runeblade", SectBannerVariantBlock.BannerType.TATTERED),
            () -> BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL));

    // --- Adept Set Statues ---
    public static final DeferredBlock<AdeptStatueBlock> ADEPT_MAGE_STATUE = BLOCKS.registerBlock("adept_mage_statue",
            AdeptStatueBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_WIZARD_STATUE = BLOCKS.registerBlock("adept_wizard_statue",
            AdeptStatueBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_RUNEBLADE_STATUE = BLOCKS.registerBlock("adept_runeblade_statue",
            AdeptStatueBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_ARTIFICER_STATUE = BLOCKS.registerBlock("adept_artificer_statue",
            AdeptStatueBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f));

    public static final DeferredBlock<AdeptStatueProxyBlock> ADEPT_STATUE_PROXY = BLOCKS.registerBlock("adept_statue_proxy",
            AdeptStatueProxyBlock::new, () -> BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(1.5f, 3600000.0f));
}
