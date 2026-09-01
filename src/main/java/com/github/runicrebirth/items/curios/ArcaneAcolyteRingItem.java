package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.network.RingCastAnimS2CPacket;
import com.github.runicrebirth.spells.types.MagicProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ArcaneAcolyteRingItem extends MagicItem implements IActivatableRing {

    public ArcaneAcolyteRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;

        MagicProjectile spell = ModSpellTypes.MAGIC_PROJECTILE.get();
        MagicData data = MagicData.of(player);

        if (data.isOnCooldown(spell.id())) return;
        if (data.globalCastLockoutTicks() > 0) return;

        double yawRad = Math.toRadians(player.getYRot());
        Vec3 fwdDir = new Vec3(Math.sin(yawRad), 0, Math.cos(yawRad));
        Vec3 rightDir = new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
        Vec3 aimStart = player.getEyePosition()
            .add(rightDir.scale(-0.35))
            //.add(fwdDir.scale(0.1))
            .add(0, -0.35, 0);
        Vec3 aimDir = player.getLookAngle();

        SpellCastContext ctx = new SpellCastContext(
            level, player, stack, aimStart, aimDir,
            player.getXRot(), player.getYRot(), null
        );

        SpellParams params = new SpellParams(
            spell.baseDamage(), spell.baseSize(),spell.spellHeight(), spell.baseSpeed(),
            spell.baseDuration(), spell.castingDelayTicks() / 2, 0,
            ModElements.ARCANE.get(), spell.damageCategory()
        );

        params.damage = params.damage * (1.0f + params.size) / 2.0f;

        spell.onCast(ctx, params);
        data.startCooldown(spell.id(), spell.cooldownTicks());
        data.setGlobalCastLockout(ServerConfig.GLOBAL_CAST_LOCKOUT_TICKS.get());
        RingCastAnimS2CPacket.send(player, 15);
    }
}
