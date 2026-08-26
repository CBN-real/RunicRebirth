package com.github.runicrebirth.damage;

import com.github.runicrebirth.api.events.SpellDamageEvent;
import com.github.runicrebirth.entities.spells.MagicShieldEntity;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public final class DamageSources {

    private DamageSources() {}

    public static boolean applyDamage(Entity target, float base, DamageSource source) {
        if (target instanceof LivingEntity living && source instanceof SpellDamageSource sds) {
            SpellDamageEvent e = new SpellDamageEvent(living, base, sds);
            if (NeoForge.EVENT_BUS.post(e).isCanceled()) return false;
            float amount = e.getAmount();
            if (sds.element() != null) {
                amount += sds.element().bonusDamage(sds.magicDamageType());
            }
            if (sds.getEntity() instanceof LivingEntity attacker) {
                if (isFriendlyFireBetween(attacker, living)) return false;
                attacker.setLastHurtMob(target);
            }
            return living.hurt(source, amount);
        }
        return target.hurt(source, base);
    }

    private static final HashMap<UUID, Integer> knockbackImmunes = new HashMap<>();

    public static void ignoreNextKnockback(LivingEntity entity) {
        if (entity.getServer() != null) {
            int tick = entity.getServer().getTickCount();
            knockbackImmunes.entrySet().removeIf(en -> tick - en.getValue() >= 10);
            knockbackImmunes.put(entity.getUUID(), tick);
        }
    }

    @SubscribeEvent
    public static void cancelKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getServer() != null && knockbackImmunes.containsKey(entity.getUUID())) {
            if (entity.getServer().getTickCount() - knockbackImmunes.get(entity.getUUID()) <= 1) {
                event.setCanceled(true);
            }
            knockbackImmunes.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void postHitEffects(LivingDamageEvent.Post event) {
        if (event.getSource() instanceof SpellDamageSource sds && sds.hasPostHitEffects()) {
            float dealt = event.getNewDamage();
            LivingEntity target = event.getEntity();
            Entity attacker = sds.getEntity();
            if (attacker instanceof LivingEntity la && sds.lifestealPercent() > 0) {
                la.heal(sds.lifestealPercent() * dealt);
            }
            if (sds.freezeTicks() > 0 && target.canFreeze()) {
                target.setTicksFrozen(target.getTicksFrozen() + sds.freezeTicks() * 2);
            }
            if (sds.fireTicks() > 0) {
                target.igniteForTicks(sds.fireTicks());
            }
            if (sds.element() != null) {
                if (target.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                    LivingEntity caster = attacker instanceof LivingEntity la ? la : null;
                    sds.element().onHitEntity(dealt, sds.magicDamageType(), caster, target, sl);
                }
            }
        }
    }

    @SubscribeEvent
    public static void preHitEffects(LivingIncomingDamageEvent event) {
        if (event.getSource() instanceof SpellDamageSource sds) {
            if (sds.iFrames() >= 0) {
                event.getContainer().setPostAttackInvulnerabilityTicks(sds.iFrames());
            }
        }

        LivingEntity target = event.getEntity();
        if (!target.level().isClientSide) {
            MagicShieldEntity shield = MagicShieldEntity.getShieldForEntity(target.level(), target);
            if (shield != null) {
                float incoming = event.getAmount();
                float absorbed = Math.min(incoming, shield.getShieldHealth());
                float remaining = incoming - absorbed;
                shield.absorbDamage(absorbed);
                if (remaining <= 0) {
                    event.setCanceled(true);
                } else {
                    event.setAmount(remaining);
                }
            }
        }
    }

    public static boolean isFriendlyFireBetween(@Nullable Entity attacker, @Nullable Entity target) {
        if (attacker == null || target == null) return false;
        if (attacker.isPassengerOfSameVehicle(target)) return true;
        if (attacker instanceof Player pa && target instanceof Player pt && !pa.canHarmPlayer(pt)) {
            return true;
        }
        if (attacker.getTeam() != null) {
            return attacker.getTeam().isAlliedTo(target.getTeam()) && !attacker.getTeam().isAllowFriendlyFire();
        }
        return attacker.isAlliedTo(target);
    }
}
