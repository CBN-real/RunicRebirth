package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class SpellCastTrigger extends SimpleCriterionTrigger<SpellCastTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, long totalCast) {
        this.trigger(player, instance -> instance.matches(totalCast));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<Long> minCount
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                Codec.LONG.optionalFieldOf("min_count")
                    .forGetter(TriggerInstance::minCount)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(long totalCast) {
            return totalCast >= minCount.orElse(0L);
        }
    }
}
