package com.github.runicrebirth.damage;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.MaceItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public final class MobDamageModifiers {

    private MobDamageModifiers() {}

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        MagicDamageType type;
        if (source instanceof SpellDamageSource sds) {
            type = sds.magicDamageType();
        } else {
            type = getWeaponType(source);
            if (type == null) return;
        }
        float mult = getMobMultiplier(event.getEntity(), type);
        if (mult != 1.0f) {
            event.setAmount(event.getAmount() * mult);
            LivingEntity target = event.getEntity();
            if (target.level() instanceof ServerLevel level) {
                double x = target.getX();
                double y = target.getY() + target.getBbHeight() * 0.5;
                double z = target.getZ();
                if (mult > 1.0f) {
                    level.sendParticles(new ScaledParticleOption(ModParticles.CRITICAL_HIT.get(), 1.4f), x, y, z, 12, 0.25, 0.25, 0.25, 0.1);
                } else {
                    level.sendParticles(new ScaledParticleOption(ModParticles.RESISTED.get(), 1.2f), x, y, z, 7, 0.15, 0.15, 0.15, 0.02);
                }
            }
        }
    }

    private static MagicDamageType getWeaponType(DamageSource source) {
        Entity direct = source.getDirectEntity();
        if (direct instanceof AbstractArrow || direct instanceof ThrownTrident) {
            return MagicDamageType.SHARP;
        }
        if (direct instanceof LivingEntity attacker) {
            var item = attacker.getMainHandItem().getItem();
            if (item instanceof MaceItem) return MagicDamageType.BLUNT;
            if (item instanceof AxeItem || attacker.getMainHandItem().has(DataComponents.WEAPON)) return MagicDamageType.SHARP;
        }
        return null;
    }

    private static float getMobMultiplier(LivingEntity entity, MagicDamageType type) {
        // Mod golem — unique resistances differ from vanilla iron golem
        if (entity instanceof RunesteelGolemEntity) {
            if (type == MagicDamageType.BLUNT) return 1.5f;
            if (type == MagicDamageType.SPIRIT) return 0.5f;
        }
        // Zombified: sharp weak, blunt resist
        else if (entity instanceof ZombifiedRunebladeAcolyteEntity
                || entity instanceof ZombifiedArtificerAcolyteEntity
                || entity instanceof ZombifiedPiglin
                || entity instanceof Zombie) {
            if (type == MagicDamageType.SHARP) return 1.25f;
            if (type == MagicDamageType.BLUNT) return 0.75f;
        }
        // Skeletal + phantom (undead flying): blunt weak, sharp resist
        else if (entity instanceof SkeletalMageAcolyteEntity
                || entity instanceof SkeletalWizardAcolyteEntity
                || entity instanceof AbstractSkeleton
                || entity instanceof Phantom) {
            if (type == MagicDamageType.BLUNT) return 1.25f;
            if (type == MagicDamageType.SHARP) return 0.75f;
        }
        // Large/heavy: blunt weak, spirit weak, sharp resist
        else if (entity instanceof IronGolem
                || entity instanceof Ravager
                || entity instanceof Warden) {
            if (type == MagicDamageType.BLUNT) return 1.25f;
            if (type == MagicDamageType.SPIRIT) return 1.25f;
            if (type == MagicDamageType.SHARP) return 0.75f;
        }
        // Human-like (illagers, villagers, witch): sharp weak, spirit resist
        else if (entity instanceof AbstractIllager
                || entity instanceof Witch
                || entity instanceof AbstractVillager) {
            if (type == MagicDamageType.SHARP) return 1.25f;
            if (type == MagicDamageType.SPIRIT) return 0.5f;
        }
        // Arthropod/creeper/elemental: spirit weak
        else if (entity instanceof Spider
                || entity instanceof Creeper
                || entity instanceof Silverfish
                || entity instanceof Endermite
                || entity instanceof Blaze
                || entity instanceof Ghast
                || entity instanceof Vex) {
            if (type == MagicDamageType.SPIRIT) return 1.25f;
        }
        // Gelatinous: blunt weak
        else if (entity instanceof Slime) {
            if (type == MagicDamageType.BLUNT) return 1.25f;
        }
        // Arcane/mystical: spirit weak
        else if (entity instanceof Guardian || entity instanceof EnderMan) {
            if (type == MagicDamageType.SPIRIT) return 1.25f;
        }
        return 1.0f;
    }
}
