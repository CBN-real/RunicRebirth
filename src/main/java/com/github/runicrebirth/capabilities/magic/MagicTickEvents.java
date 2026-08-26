package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.curios.HoverRingItem;
import com.github.runicrebirth.items.curios.RingOfPhantomMiningItem;
import com.github.runicrebirth.items.curios.ThrusterRingItem;
import com.github.runicrebirth.network.CooldownSyncS2CPacket;
import com.github.runicrebirth.network.PhantomMiningSyncS2CPacket;
import com.github.runicrebirth.network.RingDurationSyncS2CPacket;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import com.github.runicrebirth.particle.ScaledParticleOption;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class MagicTickEvents {

    private MagicTickEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        StackChangedS2CPacket.sendTo(player);
        int pm = MagicData.of(player).phantomMiningTicks();
        if (pm > 0) PhantomMiningSyncS2CPacket.sendTo(player, pm);
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MagicData data = MagicData.of(player);

        int prevThruster = data.thrusterActiveTicks();
        data.tick();

        CooldownSyncS2CPacket.sendTo(player, data.cooldowns());

        // Thruster: cooldown starts after thrust expires
        if (prevThruster > 0 && data.thrusterActiveTicks() == 0) {
            data.startCooldown(ThrusterRingItem.COOLDOWN_ID, ThrusterRingItem.TOTAL_COOLDOWN_TICKS);
        }

        if (data.thrusterActiveTicks() > 0) {
            ServerLevel level = player.serverLevel();
            Vec3 look = player.getLookAngle();
            Vec3 vel = player.getDeltaMovement();

            // Additive thrust in look direction; gravity still applies naturally
            Vec3 newVel = vel.add(look.scale(0.12));
            // Cap total speed to prevent runaway acceleration
            if (newVel.length() > 2.5) {
                newVel = newVel.normalize().scale(2.5);
            }
            player.setDeltaMovement(newVel);
            player.hurtMarked = true;

            // Fire particles from the back
            if (player.tickCount % 2 == 0) {
                Vec3 backPos = player.getEyePosition().add(look.scale(-0.75));
                ScaledParticleOption fp = new ScaledParticleOption(ModParticles.FIRE_ELEMENT.get(), 2f);
                level.sendParticles(fp, backPos.x, backPos.y, backPos.z, 5,
                    -look.x * 0.35, -look.y * 0.35, -look.z * 0.35, 0.025);
            }
        }

        if (data.isGlidingActive()) {
            ServerLevel level = player.serverLevel();
            AABB bb = player.getBoundingBox();
            boolean veryNear = !level.noCollision(player, bb.move(0, -0.25, 0));
            boolean near = veryNear || !level.noCollision(player, bb.move(0, -1.0, 0));

            Vec3 vel = player.getDeltaMovement();
            Vec3 look = player.getLookAngle();

            // Y stabilization
            double newYd;
            if (veryNear) {
                newYd = Math.min(vel.y + 0.12, 0.15);
            } else if (near) {
                newYd = Math.min(vel.y + 0.0825, 0.13);
            } else {
                newYd = Math.max(Math.min(vel.y + 0.03, 0.7), -0.06);
            }

            // Reduced horizontal drag + forward momentum from player input
            double xd = vel.x;
            double zd = vel.z;
            // Counter air drag (vanilla ~0.91 per tick → effective ~0.97)
            xd *= (1.0015 / 0.91);
            zd *= (1.0015 / 0.91);
            Vec3 newVel = new Vec3(xd, newYd, zd);

            //Add some agency for the player
            newVel = newVel.add(look.scale(0.005));

            //Cap max speed
            if (newVel.length() > 2.5) {
              newVel = newVel.normalize().scale(2.25);
            }

            player.setDeltaMovement(newVel);
            player.hurtMarked = true;
            player.fallDistance = 0;

            if (player.tickCount % 3 == 0) {
                float yawRad = (float) Math.toRadians(player.getYRot());
                double rx = Math.cos(yawRad) * 0.2;
                double rz = Math.sin(yawRad) * 0.2;
                double footY = player.getY() + 0.05;
                ScaledParticleOption p = new ScaledParticleOption(ModParticles.HOVER_EFFECT.get(), 1.0f);
                level.sendParticles(p, player.getX() + rx, footY, player.getZ() + rz, 1, 0, 0, 0, 0);
                level.sendParticles(p, player.getX() - rx, footY, player.getZ() - rz, 1, 0, 0, 0, 0);
            }
        }

        if (data.consumePhantomMiningExpired()) {
            PhantomMiningSyncS2CPacket.sendTo(player, 0);
        } else if (data.phantomMiningTicks() > 0 && player.tickCount % 20 == 0) {
            PhantomMiningSyncS2CPacket.sendTo(player, data.phantomMiningTicks());
        }

        boolean whirlwindActive = data.isWhirlwindActive();
        boolean waveThisTick = data.tickWhirlwind();
        if (whirlwindActive && player.tickCount % 2 == 0) {
            spawnWhirlwindAmbient(player);
        }
        if (waveThisTick) {
            fireWhirlwindWave(player, data.whirlwindDamage());
        }

        // Sync ring durations (thruster + hover + phantom mining) to client for overlay display
        Map<ResourceLocation, Integer> ringDurations = new HashMap<>();
        ringDurations.put(ThrusterRingItem.DURATION_KEY, data.thrusterActiveTicks());
        ringDurations.put(HoverRingItem.DURATION_KEY, data.isGlidingActive() ? -1 : 0);
        ringDurations.put(RingOfPhantomMiningItem.DURATION_KEY, data.phantomMiningTicks());
        RingDurationSyncS2CPacket.sendTo(player, ringDurations);
    }

    private static void spawnWhirlwindAmbient(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position().add(0, 1.0, 0);
        ScaledParticleOption wind = new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 1.5f);
        double baseAngle = player.tickCount * 0.25;
        for (int i = 0; i < 3; i++) {
            double angle = baseAngle + (2.0 * Math.PI * i / 3.0);
            double r = 1.2 + (i * 0.4);
            double px = center.x + Math.cos(angle) * r;
            double pz = center.z + Math.sin(angle) * r;
            double py = center.y + (i * 0.3 - 0.3);
            level.sendParticles(wind, px, py, pz, 0,
                -Math.sin(angle) * 0.25, 0.04, Math.cos(angle) * 0.25, 1.0);
        }
    }

    private static void spawnWhirlwindRadialRing(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position().add(0, 1.0, 0);
        ScaledParticleOption wind = new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 3.5f);
        int points = 16;
        for (int i = 0; i < points; i++) {
            double angle = (2.0 * Math.PI * i) / points;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            level.sendParticles(wind,
                center.x + cos * 0.5, center.y, center.z + sin * 0.5,
                0, cos * 0.45, 0.0, sin * 0.45, 1.0);
        }
    }

    private static void fireWhirlwindWave(ServerPlayer player, float damage) {
        spawnWhirlwindRadialRing(player);
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position().add(0, 1, 0);
        double radius = 3.0;
        AABB box = new AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius);

        for (net.minecraft.world.entity.LivingEntity target :
                level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box,
                e -> e != player && e.isAlive() && e.distanceTo(player) <= radius)) {
            Vec3 knockDir = target.position().subtract(center).normalize();
            target.knockback(1.5f, -knockDir.x, -knockDir.z);
            com.github.runicrebirth.damage.SpellDamageSource src =
                com.github.runicrebirth.damage.SpellDamageSource.source(
                    player,
                    com.github.runicrebirth.api.spells.MagicDamageType.BLUNT,
                    com.github.runicrebirth.init.ModElements.WIND.get());
            com.github.runicrebirth.damage.DamageSources.applyDamage(target, damage, src);

            ScaledParticleOption wind =
                new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 1.5f);
            level.sendParticles(wind,
                target.getX(), target.getY() + 1, target.getZ(),
                8, knockDir.x * 0.5, 0.3, knockDir.z * 0.5, 0.05);
        }
    }

    @SubscribeEvent
    public static void onMainHandChanged(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        StackChangedS2CPacket.sendTo(player);
    }
}
