package com.github.runicrebirth.items;

import com.github.runicrebirth.api.item.IInscribedSpell;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.magic.stack.SpellResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Abstract item that casts a pre-inscribed SpellType on right-click. Spell id stored in
 * the INSCRIBED_SPELL DataComponent; swappable at runtime via setSpell.
 */
public abstract class InscribedTool extends MagicItem implements IInscribedSpell {

    public InscribedTool(Properties properties) {
        super(properties);
    }

    @Override
    public Identifier getSpell(ItemStack stack) {
        return stack.get(ModDataComponents.INSCRIBED_SPELL.get());
    }

    @Override
    public void setSpell(ItemStack stack, Identifier spell) {
        if (spell == null) {
            stack.remove(ModDataComponents.INSCRIBED_SPELL.get());
        } else {
            stack.set(ModDataComponents.INSCRIBED_SPELL.get(), spell);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            Identifier spellId = getSpell(stack);
            if (spellId == null) return InteractionResult.PASS;
            SpellType type = SpellTypeRegistry.get(spellId);
            if (type == null) return InteractionResult.PASS;

            MagicData data = MagicData.of(sp);
            if (data.globalCastLockoutTicks() > 0 || data.isOnCooldown(type.id())) {
                return InteractionResult.PASS;
            }

            Vec3 eye = sp.getEyePosition();
            Vec3 dir = sp.getLookAngle().normalize();
            SpellCastContext ctx = new SpellCastContext((ServerLevel) level, sp, stack, eye, dir,
                sp.getXRot(), sp.getYRot(), null);

            SpellStack tmp = new SpellStack();
            tmp.append(type);
            CastResult r = SpellResolver.cast(ctx, tmp);
            if (r == CastResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
