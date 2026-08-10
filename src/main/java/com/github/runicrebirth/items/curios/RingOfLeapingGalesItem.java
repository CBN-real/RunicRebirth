package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RingOfLeapingGalesItem extends MagicItem implements IActivatableRing {

    public static final ResourceLocation COOLDOWN_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "leaping_gales_ring");
    private static final int COOLDOWN_TICKS = 100;
    private static final int SLOW_FALL_TICKS = 40;

    public RingOfLeapingGalesItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;
        MagicData data = MagicData.of(player);
        if (data.isOnCooldown(COOLDOWN_ID)) return;

        Vec3 center = player.position().add(0, 1.0, 0);
        int ringCount = 18;
        for (int i = 0; i < ringCount; i++) {
            double angle = (2 * Math.PI * i) / ringCount;
            double ox = Math.cos(angle) * 0.85;
            double oz = Math.sin(angle) * 0.85;
            level.sendParticles(
                new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 1.3f),
                center.x + ox, center.y + 0.1, center.z + oz,
                2, 0.05, 0.15, 0.05, 0.08
            );
        }
        level.sendParticles(
            new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 1.8f),
            center.x, center.y, center.z,
            14, 0.3, 0.25, 0.3, 0.14
        );

        Vec3 look = player.getLookAngle();
        double hScale = 1.0;
        double extraUp = 0.3;
        Vec3 vel = new Vec3(look.x * hScale, look.y * hScale + extraUp, look.z * hScale);
        player.setDeltaMovement(vel);
        player.hurtMarked = true;
        player.fallDistance = 0;

        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, SLOW_FALL_TICKS, 0, false, true, true));
        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
            ModSounds.SPELLS_LEAPING.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        data.startCooldown(COOLDOWN_ID, COOLDOWN_TICKS);
    }
}
