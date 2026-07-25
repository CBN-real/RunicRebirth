package com.github.runicrebirth.entities.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.github.runicrebirth.blocks.multiblock.DimensionalOculusValidator;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModItems;
import com.github.runicrebirth.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.UUID;

public class InfusionCircleEntity extends AbstractInstantSpellEntity {

    private static final RawAnimation INITIATE = RawAnimation.begin().thenPlayAndHold("initiate_spell");

    private UUID casterUUID;

    public InfusionCircleEntity(EntityType<? extends InfusionCircleEntity> type, Level level) {
        super(type, level);
        this.chargeTicks = 90; // 4.5 seconds
        this.endTicks = 20;
    }

    public InfusionCircleEntity(Level level, LivingEntity caster, SpellParams params) {
        this(ModEntities.INFUSION_CIRCLE.get(), level);
        this.casterUUID = caster.getUUID();
        initFromParams(params);
    }

    @Override
    protected void onActivated() {
        if (!(this.level() instanceof ServerLevel server)) return;
        Vec3 center = this.position();
        boolean didSomething = false;

        BlockPos centerBlock = BlockPos.containing(center);

        // Check for InfusionAltarBlock at or near center
        for (BlockPos pos : BlockPos.betweenClosed(centerBlock.offset(-2, -1, -2), centerBlock.offset(2, 1, 2))) {
            BlockState state = server.getBlockState(pos);
            if (state.is(ModBlocks.INFUSION_ALTAR.get())) {
                var be = server.getBlockEntity(pos);
                if (be instanceof InfusionAltarBlockEntity altar) {
                    if (!altar.isActive()) {
                        if (altar.tryActivate()) {
                            RunicRebirth.LOGGER.info("[Infusion] Altar activated at {}", pos);
                            didSomething = true;
                        }
                    } else if (altar.tryStartCrafting()) {
                        RunicRebirth.LOGGER.info("[Infusion] Altar crafting started at {}", pos);
                        didSomething = true;
                    }
                }
                break;
            }
        }

        // Check for RunicAnvilBlock at or near center
        for (BlockPos pos : BlockPos.betweenClosed(centerBlock.offset(-2, -1, -2), centerBlock.offset(2, 1, 2))) {
            BlockState state = server.getBlockState(pos);
            if (state.is(ModBlocks.RUNIC_ANVIL.get())) {
                var be = server.getBlockEntity(pos);
                if (be instanceof RunicAnvilBlockEntity anvil) {
                    if (!anvil.isActive()) {
                        if (anvil.tryActivate()) {
                            didSomething = true;
                        }
                    } else if (anvil.tryStartCrafting()) {
                        didSomething = true;
                    }
                }
                break;
            }
        }

        // Check for OculusPortalBlock at or near center
        for (BlockPos pos : BlockPos.betweenClosed(centerBlock.offset(-2, -1, -2), centerBlock.offset(2, 1, 2))) {
            BlockState state = server.getBlockState(pos);
            if (state.is(ModBlocks.OCULUS_PORTAL.get())) {
                var result = DimensionalOculusValidator.validateFull(server, pos);
                if (result.valid()) {
                    activateMultiblock(server, result);
                    RunicRebirth.LOGGER.info("[Infusion] Dimensional Oculus activated at {}", pos);
                    didSomething = true;
                } else {
                    RunicRebirth.LOGGER.info("[Infusion] Dimensional Oculus incomplete at {}", pos);
                }
                break;
            }
        }

        // Convert stone ItemEntities to runic stone
        AABB scanBox = new AABB(center.x - 2, center.y - 1, center.z - 2,
                center.x + 2, center.y + 2, center.z + 2);
        List<ItemEntity> items = server.getEntitiesOfClass(ItemEntity.class, scanBox);
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (stack.is(Items.STONE)) {
                int count = stack.getCount();
                itemEntity.setItem(new ItemStack(ModItems.RUNIC_STONE.get(), count));
                didSomething = true;
            }
        }

        if (didSomething) {
            ParticleHelper.burstParticleEvent(server, element().particle(), center,
                    30, 0.5, 0.5, 0.5, 0.05, 1.0f);
        }

        beginEnding();
    }

    private void activateMultiblock(ServerLevel level, DimensionalOculusValidator.ValidationResult result) {
        if (result.controllerPos() != null) {
            var be = level.getBlockEntity(result.controllerPos());
            if (be instanceof OculusControllerBlockEntity controller) {
                controller.activate(result.portalPos(), result.pillarPositions());
                RunicRebirth.LOGGER.info("[Infusion] Controller activated at {}", result.controllerPos());
            }
        }

        for (BlockPos pos : result.pillarPositions()) {
            if (level.getBlockState(pos).is(ModBlocks.RUNIC_STONE_PILLAR.get())) {
                level.setBlock(pos, ModBlocks.OCULUS_PILLAR.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        if (result.portalPos() != null) {
            var portalBe = level.getBlockEntity(result.portalPos());
            if (portalBe instanceof OculusPortalBlockEntity portal) {
                portal.setControllerPos(result.controllerPos());
                portal.setAnimState(OculusPortalBlockEntity.AnimState.IDLE);
            }
        }
    }

    @Override
    protected void onActiveTick() {}

    @Override
    protected void spawnActiveParticles() {
        Vec3 pos = this.position();
        ParticleHelper.areaParticleEvent(this.level(), element().particle(), pos, 8.0, 1, this.size);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spell_phase", 0, state -> {
            state.setAnimation(INITIATE);
            return PlayState.CONTINUE;
        }));
    }
}
