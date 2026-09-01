package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlinkRingItem extends MagicItem implements IActivatableRing {

    public static final Identifier COOLDOWN_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "blink_ring");
    private static final int COOLDOWN_TICKS = 100;
    private static final double BLINK_RANGE = 8.0;

    public BlinkRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;
        MagicData data = MagicData.of(player);
        if (data.isOnCooldown(COOLDOWN_ID)) return;

        Vec3 from = player.position();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();
        Vec3 rayEnd = eyePos.add(lookDir.scale(BLINK_RANGE));

        BlockHitResult blockHit = level.clip(new ClipContext(
            eyePos, rayEnd,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player));

        Vec3 dest;
        if (blockHit.getType() != HitResult.Type.MISS) {
            double hitDist = eyePos.distanceTo(blockHit.getLocation());
            double teleportDist = Math.max(0, hitDist - 0.6);
            dest = from.add(lookDir.scale(teleportDist));
        } else {
            dest = from.add(lookDir.scale(BLINK_RANGE));
        }

        spawnBlinkParticles(level, from, dest);
        player.teleportTo(dest.x, dest.y, dest.z);
        player.fallDistance = 0;
        level.playSound(null, dest.x, dest.y, dest.z,
            ModSounds.SPELLS_BLINK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        data.startCooldown(COOLDOWN_ID, COOLDOWN_TICKS);
    }

    private static void spawnBlinkParticles(ServerLevel level, Vec3 from, Vec3 dest) {
        ScaledParticleOption ap = new ScaledParticleOption(ModParticles.ARCANE_ELEMENT.get(), 0.6f);

        // Source: particles burst outward where player disappeared
        for (int i = 0; i < 12; i++) {
            double vx = (level.getRandom().nextDouble() - 0.5) * 0.5;
            double vy = (level.getRandom().nextDouble() - 0.5) * 0.5;
            double vz = (level.getRandom().nextDouble() - 0.5) * 0.5;
            level.sendParticles(ap, from.x, from.y + 1.0, from.z, 1, vx, vy, vz, 0);
        }

        // Destination: particles spawn scattered in a sphere and converge inward
        for (int i = 0; i < 24; i++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
            double elev = (level.getRandom().nextDouble() - 0.5) * Math.PI;
            double radius = 1.0 + level.getRandom().nextDouble() * 1.5;
            double ox = Math.cos(angle) * Math.cos(elev) * radius;
            double oy = Math.sin(elev) * radius;
            double oz = Math.sin(angle) * Math.cos(elev) * radius;
            // Velocity points inward toward dest center
            double len = Math.sqrt(ox * ox + oy * oy + oz * oz);
            double speed = 0.3 + level.getRandom().nextDouble() * 0.1;
            level.sendParticles(ap,
                dest.x + ox, dest.y + 1.0 + oy, dest.z + oz,
                1, -ox / len * speed, -oy / len * speed, -oz / len * speed, 0.01);
        }
    }
}
