package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ElementTrialTrigger extends SimpleCriterionTrigger<ElementTrialTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation elementId) {
        this.trigger(player, instance -> instance.matches(elementId));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<ResourceLocation> element
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                                .forGetter(TriggerInstance::player),
                        ResourceLocation.CODEC.optionalFieldOf("element")
                                .forGetter(TriggerInstance::element)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(ResourceLocation elementId) {
            return element.isEmpty() || element.get().equals(elementId);
        }
    }
}
