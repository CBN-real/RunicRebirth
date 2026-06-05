package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.InfusionAltarBlock;
import com.github.runicrebirth.blocks.InfusionAltarProxyBlock;
import com.github.runicrebirth.blocks.OculusPillarBlock;
import com.github.runicrebirth.blocks.OculusPortalBlock;
import com.github.runicrebirth.blocks.ReturnPortalBlock;
import com.github.runicrebirth.blocks.RunesteelPylonBlock;
import com.github.runicrebirth.blocks.OculusControllerBlock;
import com.github.runicrebirth.blocks.TrialSpawnerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    // Dungeon blocks
    public static final DeferredBlock<ReturnPortalBlock> RETURN_PORTAL = BLOCKS.register("return_portal",
            () -> new ReturnPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 12)));

    public static final DeferredBlock<TrialSpawnerBlock> TRIAL_SPAWNER = BLOCKS.register("trial_spawner",
            () -> new TrialSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().lightLevel(s -> 5)));
}
