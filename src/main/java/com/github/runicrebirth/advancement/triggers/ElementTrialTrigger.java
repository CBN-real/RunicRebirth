package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ElementTrialTrigger extends SimpleCriterionTrigger<ElementTrialTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Identifier elementId) {
        this.trigger(player, instance -> instance.matches(elementId));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<Identifier> element
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                                .forGetter(TriggerInstance::player),
                        Identifier.CODEC.optionalFieldOf("element")
                                .forGetter(TriggerInstance::element)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(Identifier elementId) {
            return element.isEmpty() || element.get().equals(elementId);
        }
    }
}
