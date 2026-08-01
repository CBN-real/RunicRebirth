package com.github.runicrebirth.damage;

import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public final class MobDamageModifiers {

    private MobDamageModifiers() {}

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource() instanceof SpellDamageSource sds)) return;
        MagicDamageType type = sds.magicDamageType();
        LivingEntity entity = event.getEntity();
        float mult = 1.0f;
        if (entity instanceof RunesteelGolemEntity) {
            if (type == MagicDamageType.BLUNT) mult = 1.5f;
            else if (type == MagicDamageType.SPIRIT) mult = 0.5f;
        } else if (entity instanceof ZombifiedRunebladeAcolyteEntity
                || entity instanceof ZombifiedArtificerAcolyteEntity
                || entity instanceof Zombie) {
            if (type == MagicDamageType.SHARP) mult = 1.5f;
            else if (type == MagicDamageType.BLUNT) mult = 0.75f;
        } else if (entity instanceof SkeletalMageAcolyteEntity
                || entity instanceof SkeletalWizardAcolyteEntity
                || entity instanceof AbstractSkeleton) {
            if (type == MagicDamageType.BLUNT) mult = 1.5f;
            else if (type == MagicDamageType.SHARP) mult = 0.75f;
        }
        if (mult != 1.0f) event.setAmount(event.getAmount() * mult);
    }
}
