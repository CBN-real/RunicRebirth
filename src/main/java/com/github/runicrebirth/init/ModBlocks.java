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
import com.github.runicrebirth.blocks.RunelightBlock;
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
import net.minecraft.world.level.block.ChainBlock;
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

    public static final DeferredBlock<Block> RUNIC_STONE = BLOCKS.register("runic_stone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<SlabBlock> RUNIC_STONE_SLAB = BLOCKS.register("runic_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<StairBlock> RUNIC_STONE_STAIRS = BLOCKS.register("runic_stone_stairs",
            () -> new StairBlock(RUNIC_STONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<WallBlock> RUNIC_STONE_WALL = BLOCKS.register("runic_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> RUNIC_STONE_PILLAR = BLOCKS.register("runic_stone_pillar",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    public static final DeferredBlock<Block> RUNIC_STONE_BRICKS = BLOCKS.register("runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<SlabBlock> RUNIC_STONE_BRICKS_SLAB = BLOCKS.register("runic_stone_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<StairBlock> RUNIC_STONE_BRICKS_STAIRS = BLOCKS.register("runic_stone_bricks_stairs",
        () -> new StairBlock(RUNIC_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<WallBlock> RUNIC_STONE_BRICKS_WALL = BLOCKS.register("runic_stone_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> ARCANE_RUNIC_STONE_BRICKS = BLOCKS.register("arcane_runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(s -> 4)));

    public static final DeferredBlock<SlabBlock> ARCANE_RUNIC_STONE_BRICKS_SLAB = BLOCKS.register("arcane_runic_stone_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(s -> 4)));

    public static final DeferredBlock<StairBlock> ARCANE_RUNIC_STONE_BRICKS_STAIRS = BLOCKS.register("arcane_runic_stone_bricks_stairs",
        () -> new StairBlock(ARCANE_RUNIC_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(s -> 4)));

    public static final DeferredBlock<WallBlock> ARCANE_RUNIC_STONE_BRICKS_WALL = BLOCKS.register("arcane_runic_stone_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).lightLevel(s -> 4)));

    public static final DeferredBlock<Block> CRACKED_RUNIC_STONE_BRICKS = BLOCKS.register("cracked_runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<SlabBlock> CRACKED_RUNIC_STONE_BRICKS_SLAB = BLOCKS.register("cracked_runic_stone_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<StairBlock> CRACKED_RUNIC_STONE_BRICKS_STAIRS = BLOCKS.register("cracked_runic_stone_bricks_stairs",
        () -> new StairBlock(CRACKED_RUNIC_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<WallBlock> CRACKED_RUNIC_STONE_BRICKS_WALL = BLOCKS.register("cracked_runic_stone_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> FROZEN_RUNIC_BRICKS = BLOCKS.register("frozen_runic_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<SlabBlock> FROZEN_RUNIC_BRICKS_SLAB = BLOCKS.register("frozen_runic_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<StairBlock> FROZEN_RUNIC_BRICKS_STAIRS = BLOCKS.register("frozen_runic_bricks_stairs",
        () -> new StairBlock(FROZEN_RUNIC_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<WallBlock> FROZEN_RUNIC_BRICKS_WALL = BLOCKS.register("frozen_runic_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> FLAMING_RUNIC_BRICKS = BLOCKS.register("flaming_runic_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<SlabBlock> FLAMING_RUNIC_BRICKS_SLAB = BLOCKS.register("flaming_runic_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<StairBlock> FLAMING_RUNIC_BRICKS_STAIRS = BLOCKS.register("flaming_runic_bricks_stairs",
        () -> new StairBlock(FLAMING_RUNIC_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<WallBlock> FLAMING_RUNIC_BRICKS_WALL = BLOCKS.register("flaming_runic_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> EARTHEN_RUNIC_BRICKS = BLOCKS.register("earthen_runic_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<SlabBlock> EARTHEN_RUNIC_BRICKS_SLAB = BLOCKS.register("earthen_runic_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<StairBlock> EARTHEN_RUNIC_BRICKS_STAIRS = BLOCKS.register("earthen_runic_bricks_stairs",
        () -> new StairBlock(EARTHEN_RUNIC_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<WallBlock> EARTHEN_RUNIC_BRICKS_WALL = BLOCKS.register("earthen_runic_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> WINDSWEPT_RUNIC_BRICKS = BLOCKS.register("windswept_runic_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<SlabBlock> WINDSWEPT_RUNIC_BRICKS_SLAB = BLOCKS.register("windswept_runic_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<StairBlock> WINDSWEPT_RUNIC_BRICKS_STAIRS = BLOCKS.register("windswept_runic_bricks_stairs",
        () -> new StairBlock(WINDSWEPT_RUNIC_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<WallBlock> WINDSWEPT_RUNIC_BRICKS_WALL = BLOCKS.register("windswept_runic_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> MOSSY_RUNIC_BRICKS = BLOCKS.register("mossy_runic_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<SlabBlock> MOSSY_RUNIC_BRICKS_SLAB = BLOCKS.register("mossy_runic_bricks_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<StairBlock> MOSSY_RUNIC_BRICKS_STAIRS = BLOCKS.register("mossy_runic_bricks_stairs",
        () -> new StairBlock(MOSSY_RUNIC_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final DeferredBlock<WallBlock> MOSSY_RUNIC_BRICKS_WALL = BLOCKS.register("mossy_runic_bricks_wall",
        () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)));

    public static final DeferredBlock<Block> RUNESTEEL_BLOCK = BLOCKS.register("runesteel_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> FLAMING_RUNESTEEL_BLOCK = BLOCKS.register("flaming_runesteel_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> WINDSWEPT_RUNESTEEL_BLOCK = BLOCKS.register("windswept_runesteel_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> FROZEN_RUNESTEEL_BLOCK = BLOCKS.register("frozen_runesteel_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<Block> EARTHEN_RUNESTEEL_BLOCK = BLOCKS.register("earthen_runesteel_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<ChainBlock> RUNESTEEL_CHAIN = BLOCKS.register("runesteel_chain",
        () -> new ChainBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHAIN).noOcclusion()));

    public static final DeferredBlock<Block> FALSE_SKY = BLOCKS.register("false_sky",
        () -> new Block(
            Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> true)
                .lightLevel(s -> 7)));

    public static final DeferredBlock<Block> CRACKED_FALSE_SKY = BLOCKS.register("cracked_false_sky",
        () -> new Block(
            Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> true)
                .lightLevel(s -> 7)));

    public static final DeferredBlock<Block> CUT_RUNIC_STONE = BLOCKS.register("cut_runic_stone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<Block> REINFORCED_CUT_RUNIC_STONE = BLOCKS.register("reinforced_cut_runic_stone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<OculusPortalBlock> OCULUS_PORTAL = BLOCKS.register("oculus_portal",
            () -> new OculusPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL).noOcclusion().lightLevel(s -> 12)));

    public static final DeferredBlock<OculusControllerBlock> OCULUS_CONTROLLER = BLOCKS.register("oculus_controller",
            () -> new OculusControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL).noOcclusion()));

    public static final DeferredBlock<OculusPillarBlock> OCULUS_PILLAR = BLOCKS.register("oculus_pillar",
            () -> new OculusPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7)));

    public static final DeferredBlock<RunesteelPylonBlock> RUNESTEEL_PYLON = BLOCKS.register("runesteel_pylon",
            () -> new RunesteelPylonBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7)));

    public static final DeferredBlock<InfusionAltarBlock> INFUSION_ALTAR = BLOCKS.register("infusion_altar",
            () -> new InfusionAltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 7)));

    public static final DeferredBlock<InfusionAltarProxyBlock> INFUSION_ALTAR_PROXY = BLOCKS.register("infusion_altar_proxy",
            () -> new InfusionAltarProxyBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion().noLootTable().replaceable()));

    public static final DeferredBlock<RunicAnvilBlock> RUNIC_ANVIL = BLOCKS.register("runic_anvil",
            () -> new RunicAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 5)));

    public static final DeferredBlock<RunesteelCacheBlock> RUNESTEEL_CACHE = BLOCKS.register("runesteel_cache",
            () -> new RunesteelCacheBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 3)));

    public static final DeferredBlock<RunesteelPortcullisBlock> RUNESTEEL_PORTCULLIS = BLOCKS.register("runesteel_portcullis",
            () -> new RunesteelPortcullisBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).noOcclusion()));

    // Dungeon blocks
    public static final DeferredBlock<ReturnPortalBlock> RETURN_PORTAL = BLOCKS.register("return_portal",
            () -> new ReturnPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 12)));

    public static final DeferredBlock<DungeonMobSpawnerBlock> DUNGEON_MOB_SPAWNER = BLOCKS.register("dungeon_mob_spawner",
            () -> new DungeonMobSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 5)));

    public static final DeferredBlock<DungeonRoomTrackerBlock> DUNGEON_ROOM_TRACKER = BLOCKS.register("dungeon_room_tracker",
            () -> new DungeonRoomTrackerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 3)));

    public static final DeferredBlock<DungeonDoorBlock> DUNGEON_DOOR = BLOCKS.register("dungeon_door",
            () -> new DungeonDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR).noOcclusion().strength(5.0f)));

    public static final DeferredBlock<DungeonDoorProxyBlock> DUNGEON_DOOR_PROXY = BLOCKS.register("dungeon_door_proxy",
            () -> new DungeonDoorProxyBlock(BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(5.0f, 3600000.0f)));

    public static final DeferredBlock<RunelightTorchBlock> RUNELIGHT_TORCH = BLOCKS.register("runelight_torch",
            () -> new RunelightTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).noOcclusion().lightLevel(s -> 15)));

    public static final DeferredBlock<RunelightWallTorchBlock> RUNELIGHT_WALL_TORCH = BLOCKS.register("runelight_wall_torch",
            () -> new RunelightWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).noOcclusion().lightLevel(s -> 15)));

    public static final DeferredBlock<RunelightLanternBlock> RUNELIGHT_LANTERN = BLOCKS.register("runelight_lantern",
            () -> new RunelightLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).noOcclusion().lightLevel(s -> 15).destroyTime(0.5f)));

    public static final DeferredBlock<RunelightBlock> RUNELIGHT = BLOCKS.register("runelight",
            () -> new RunelightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).noOcclusion().lightLevel(s -> 18)));

    // Dungeon trap blocks
    public static final DeferredBlock<DungeonTemporaryPlatformBlock> DUNGEON_TEMPORARY_PLATFORM = BLOCKS.register("dungeon_temporary_platform",
            () -> new DungeonTemporaryPlatformBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(2.0f).noOcclusion()));

    public static final DeferredBlock<DungeonPressurePlateBlock> DUNGEON_PRESSURE_PLATE = BLOCKS.register("dungeon_pressure_plate",
            () -> new DungeonPressurePlateBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission().strength(0.5f)));

    public static final DeferredBlock<DungeonSpikeBlock> DUNGEON_SPIKE = BLOCKS.register("dungeon_spike",
            () -> new DungeonSpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(2.0f)));

    public static final DeferredBlock<CrumblingPlatformBlock> CRUMBLING_PLATFORM = BLOCKS.register("crumbling_platform",
            () -> new CrumblingPlatformBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(0.5f).noOcclusion()));

    // Dungeon trap geo blocks
    public static final DeferredBlock<DungeonBoulderSpawnerBlock> DUNGEON_BOULDER_SPAWNER = BLOCKS.register("dungeon_boulder_spawner",
            () -> new DungeonBoulderSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(4.0f)));

    public static final DeferredBlock<DungeonSwingingAxeBlock> DUNGEON_SWINGING_AXE = BLOCKS.register("dungeon_swinging_axe",
            () -> new DungeonSwingingAxeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(4.0f)));

    public static final DeferredBlock<DungeonFlamethrowerBlock> DUNGEON_FLAMETHROWER = BLOCKS.register("dungeon_flamethrower",
            () -> new DungeonFlamethrowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(2.0f)));

    public static final DeferredBlock<AncientArcaneTurretBlock> ANCIENT_ARCANE_TURRET = BLOCKS.register("ancient_arcane_turret",
            () -> new AncientArcaneTurretBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(3.0f).lightLevel(s -> 4)));

    public static final DeferredBlock<MeditationCushionBlock> MEDITATION_CUSHION = BLOCKS.register("meditation_cushion",
            () -> new MeditationCushionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).strength(0.5f).noOcclusion()));

    public static final DeferredBlock<RunicLeverBlock> RUNIC_LEVER = BLOCKS.register("runic_lever",
            () -> new RunicLeverBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f)));

    // --- Sect Banner blocks ---
    public static final DeferredBlock<SectBannerBlock> SECT_BANNER = BLOCKS.register("sect_banner",
            () -> new SectBannerBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion()
                    .sound(SoundType.WOOL)));

    public static final DeferredBlock<TatteredSectBannerBlock> TATTERED_SECT_BANNER = BLOCKS.register("tattered_sect_banner",
            () -> new TatteredSectBannerBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion()
                    .sound(SoundType.WOOL)));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_MAGE = BLOCKS.register("sect_banner_mage",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "sect_banner_mage", SectBannerVariantBlock.BannerType.SECT));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_ARTIFICER = BLOCKS.register("sect_banner_artificer",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "sect_banner_artificer", SectBannerVariantBlock.BannerType.SECT));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_WIZARD = BLOCKS.register("sect_banner_wizard",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "sect_banner_wizard", SectBannerVariantBlock.BannerType.SECT));

    public static final DeferredBlock<SectBannerVariantBlock> SECT_BANNER_RUNEBLADE = BLOCKS.register("sect_banner_runeblade",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "sect_banner_runeblade", SectBannerVariantBlock.BannerType.SECT));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_MAGE = BLOCKS.register("tattered_sect_banner_mage",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "tattered_sect_banner_mage", SectBannerVariantBlock.BannerType.TATTERED));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_ARTIFICER = BLOCKS.register("tattered_sect_banner_artificer",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "tattered_sect_banner_artificer", SectBannerVariantBlock.BannerType.TATTERED));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_WIZARD = BLOCKS.register("tattered_sect_banner_wizard",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "tattered_sect_banner_wizard", SectBannerVariantBlock.BannerType.TATTERED));

    public static final DeferredBlock<SectBannerVariantBlock> TATTERED_SECT_BANNER_RUNEBLADE = BLOCKS.register("tattered_sect_banner_runeblade",
            () -> new SectBannerVariantBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.WOOL),
                    "tattered_sect_banner_runeblade", SectBannerVariantBlock.BannerType.TATTERED));

    // --- Adept Set Statues ---
    public static final DeferredBlock<AdeptStatueBlock> ADEPT_MAGE_STATUE = BLOCKS.register("adept_mage_statue",
            () -> new AdeptStatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f)));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_WIZARD_STATUE = BLOCKS.register("adept_wizard_statue",
            () -> new AdeptStatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f)));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_RUNEBLADE_STATUE = BLOCKS.register("adept_runeblade_statue",
            () -> new AdeptStatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f)));

    public static final DeferredBlock<AdeptStatueBlock> ADEPT_ARTIFICER_STATUE = BLOCKS.register("adept_artificer_statue",
            () -> new AdeptStatueBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().strength(1.5f)));

    public static final DeferredBlock<AdeptStatueProxyBlock> ADEPT_STATUE_PROXY = BLOCKS.register("adept_statue_proxy",
            () -> new AdeptStatueProxyBlock(BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(1.5f, 3600000.0f)));
}
