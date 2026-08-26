package com.github.runicrebirth.damage;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import com.github.runicrebirth.entities.spells.MagicBlastEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.items.RunicDaggerItem;
import com.github.runicrebirth.items.RunicShieldItem;
import com.github.runicrebirth.magic.stack.SpellResolver;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class WeaponEventHandler {

    private WeaponEventHandler() {}

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.getMainHandItem().getItem() instanceof RunicDaggerItem)) return;
        if (MagicData.of(player).thrownDaggerEntityId() != -1) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.getUseItem().getItem() instanceof RunicShieldItem)) return;
        MagicData data = MagicData.of(player);
        if (data.isOnCooldown(RunicShieldItem.COOLDOWN_ID)) return;

        DamageSource source = event.getDamageSource();
        Vec3 attackerPos = null;
        if (source.getDirectEntity() != null) {
            attackerPos = source.getDirectEntity().position();
        } else if (source.getSourcePosition() != null) {
            attackerPos = source.getSourcePosition();
        }
        if (attackerPos == null) return;

        Vec3 dir = attackerPos.subtract(player.position()).normalize();
        ServerLevel level = player.serverLevel();
        Vec3 spawnPos = player.getEyePosition().add(dir.scale(0.5));

        SpellCastContext ctx = new SpellCastContext(
            level, player, player.getOffhandItem(), spawnPos, dir, player.getXRot(), player.getYRot(), null);
        SpellStack tmp = new SpellStack();
        tmp.append(ModSpellTypes.MAGIC_BLAST.get());
        SpellParams params = SpellResolver.buildParams(ctx, tmp);
        if (params != null) {
            MagicBlastEntity blast = new MagicBlastEntity(ModEntities.MAGIC_BLAST.get(), level);
            blast.init(player, spawnPos, dir, params);
            level.addFreshEntity(blast);
        }

        data.startCooldown(RunicShieldItem.COOLDOWN_ID, RunicShieldItem.COOLDOWN_TICKS);
    }
}
