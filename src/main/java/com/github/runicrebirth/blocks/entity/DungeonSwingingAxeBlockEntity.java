package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.DungeonSwingingAxeBlock;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DungeonSwingingAxeBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation SWINGING_AXE = RawAnimation.begin().thenLoop("swinging_axe");

    // Full cycle: 40 ticks = 2s  (sin: 0→+65°→0→-65°→0)
    private static final int HALF_CYCLE = 20;
    private static final int FULL_CYCLE = 40;
    private static final double SWING_RADIUS = 3.5;
    private static final double MAX_ANGLE_DEG = 65.0;
    private static final float SHARP_DAMAGE = 30.0f; // 15 hearts

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int swingTick = 0;
    // Track hit entities per half-swing to prevent multi-hit per pass
    private final Set<UUID> hitThisPass = new HashSet<>();
    private boolean lastPassWasForward = true;

    public DungeonSwingingAxeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_SWINGING_AXE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                   DungeonSwingingAxeBlockEntity be) {
        be.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        swingTick = (swingTick + 1) % FULL_CYCLE;

        // Detect direction change to reset hit set
        boolean currentlyForward = swingTick < HALF_CYCLE;
        if (currentlyForward != lastPassWasForward) {
            hitThisPass.clear();
            lastPassWasForward = currentlyForward;
        }

        // Matches animation: math.sin(anim_time * 180) * 65
        double angleDeg = Math.sin(Math.PI * swingTick / HALF_CYCLE) * MAX_ANGLE_DEG;
        double angleRad = Math.toRadians(angleDeg);

        // Mount point = center of block (top of block entity's 1×1×1 space)
        Vec3 mount = Vec3.atCenterOf(pos);

        // Axe swings in the plane defined by FACING direction and vertical axis.
        // FACING=NORTH → blade oscillates N/S (along Z-axis in Minecraft: NORTH=-Z)
        Direction facing = state.getValue(DungeonSwingingAxeBlock.FACING).getClockWise();
        double nx = facing.getStepX();
        double nz = facing.getStepZ();

        // Blade center at angle from vertical (0=straight down, positive=toward FACING)
        double bx = mount.x + Math.sin(angleRad) * nx * SWING_RADIUS;
        double by = mount.y - Math.cos(angleRad) * SWING_RADIUS;
        double bz = mount.z + Math.sin(angleRad) * nz * SWING_RADIUS;

        // Blade AABB: 2 wide (perpendicular to swing plane), 0.25 thick, 1 tall
        double halfPerp = 1.0;   // half of 2-block width
        double halfThick = 0.125; // half of 0.25 thickness (along swing arc direction)
        double halfH = 0.5;

        AABB bladeBB = new AABB(
                bx - halfPerp, by - halfH, bz - halfPerp,
                bx + halfPerp, by + halfH, bz + halfPerp
        );

        DungeonInstance inst = DungeonInstanceManager.get().getInstanceForPosition(pos);
        float sharpMult = inst != null ? inst.getSharpTrapMultiplier() : 1.0f;

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, bladeBB);
        for (LivingEntity target : targets) {
            if (hitThisPass.contains(target.getUUID())) continue;
            target.hurt(level.damageSources().generic(), SHARP_DAMAGE * sharpMult);
            hitThisPass.add(target.getUUID());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                state -> state.setAndContinue(SWINGING_AXE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
