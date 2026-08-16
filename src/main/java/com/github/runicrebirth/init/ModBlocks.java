package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
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

    public static final DeferredBlock<Block> RUNIC_STONE_PILLAR = BLOCKS.register("runic_stone_pillar",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    public static final DeferredBlock<Block> RUNIC_STONE_BRICKS = BLOCKS.register("runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final DeferredBlock<Block> ARCANE_RUNIC_STONE_BRICKS = BLOCKS.register("arcane_runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(s -> 4)));

    public static final DeferredBlock<Block> CRACKED_RUNIC_STONE_BRICKS = BLOCKS.register("cracked_runic_stone_bricks",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

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
}
