package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.RunesteelPortcullisBlock;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RunesteelPortcullisBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation ANIM_OPEN_TOP = RawAnimation.begin().thenPlayAndHold("open_top");
    private static final RawAnimation ANIM_CLOSING  = RawAnimation.begin().thenPlayAndHold("closing");

    // Matches the HEIGHT*20 scheduled tick in RunesteelPortcullisBlock (20 ticks = 1 second per unit)
    private static final float TICKS_PER_UNIT = 20.0f;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunesteelPortcullisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNESTEEL_PORTCULLIS.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 0, this::handleAnimation));
    }

    private PlayState handleAnimation(AnimationState<RunesteelPortcullisBlockEntity> state) {
        BlockState bs = getBlockState();
        boolean open    = bs.getValue(RunesteelPortcullisBlock.OPEN);
        boolean closing = bs.getValue(RunesteelPortcullisBlock.CLOSING);
        int     height  = bs.getValue(RunesteelPortcullisBlock.HEIGHT);

        if (open) {
            state.setControllerSpeed(1.0f);
            return state.setAndContinue(ANIM_OPEN_TOP);
        }

        float targetTick = Math.max(1, Math.min(9, height)) * TICKS_PER_UNIT;
        double currentTick = state.getAnimationTick();

        if (closing) {
            // Gate actively dropping — play at normal speed
            state.setControllerSpeed(1.0f);
        } else if (currentTick >= targetTick) {
            // At or past target frame — hold here
            state.setControllerSpeed(0.0f);
        } else {
            // Fresh chunk load with gate already stopped — fast-forward to target in one tick
            state.setControllerSpeed((float)(targetTick - currentTick));
        }

        return state.setAndContinue(ANIM_CLOSING);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
