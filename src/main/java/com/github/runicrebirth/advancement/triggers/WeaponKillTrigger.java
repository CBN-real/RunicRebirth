package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class WeaponKillTrigger extends SimpleCriterionTrigger<WeaponKillTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int totalKills) {
        this.trigger(player, instance -> instance.matches(totalKills));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<Integer> minCount
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                Codec.INT.optionalFieldOf("min_count")
                    .forGetter(TriggerInstance::minCount)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(int totalKills) {
            return totalKills >= minCount.orElse(0);
        }
    }
}
