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
            data.startCooldown(RingOfPhantomMiningItem.COOLDOWN_ID, RingOfPhantomMiningItem.COOLDOWN_TICKS);
        } else if (data.phantomMiningTicks() > 0 && player.tickCount % 20 == 0) {
            PhantomMiningSyncS2CPacket.sendTo(player, data.phantomMiningTicks());
        }

        // Sync ring durations (thruster + hover) to client for overlay display
        Map<ResourceLocation, Integer> ringDurations = new HashMap<>();
        ringDurations.put(ThrusterRingItem.DURATION_KEY, data.thrusterActiveTicks());
        ringDurations.put(HoverRingItem.DURATION_KEY, data.isGlidingActive() ? -1 : 0);
        RingDurationSyncS2CPacket.sendTo(player, ringDurations);
    }

    @SubscribeEvent
    public static void onMainHandChanged(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        StackChangedS2CPacket.sendTo(player);
    }
}
