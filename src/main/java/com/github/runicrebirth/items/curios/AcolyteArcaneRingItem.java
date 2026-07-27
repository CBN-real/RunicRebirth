package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.items.MagicItem;
import com.github.runicrebirth.spells.types.MagicProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AcolyteArcaneRingItem extends MagicItem implements IActivatableRing {

    public AcolyteArcaneRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void activate(ServerPlayer player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level)) return;

        MagicProjectile spell = ModSpellTypes.MAGIC_PROJECTILE.get();
        MagicData data = MagicData.of(player);

        if (data.isOnCooldown(spell.id())) return;
        if (data.globalCastLockoutTicks() > 0) return;

        Vec3 aimStart = player.getEyePosition();
        Vec3 aimDir = player.getLookAngle();

        SpellCastContext ctx = new SpellCastContext(
            level, player, stack, aimStart, aimDir,
            player.getXRot(), player.getYRot(), null
        );

        SpellParams params = new SpellParams(
            spell.baseDamage(), spell.baseSize(), spell.baseSpeed(),
            spell.baseDuration(), spell.castingDelayTicks(), 0,
            ModElements.ARCANE.get(), spell.damageCategory()
        );

        spell.onCast(ctx, params);
        data.startCooldown(spell.id(), spell.cooldownTicks());
        data.setGlobalCastLockout(ServerConfig.GLOBAL_CAST_LOCKOUT_TICKS.get());
    }
}
