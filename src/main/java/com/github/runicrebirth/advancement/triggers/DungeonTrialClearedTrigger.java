package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class DungeonTrialClearedTrigger extends SimpleCriterionTrigger<DungeonTrialClearedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation trialId) {
        this.trigger(player, instance -> instance.matches(trialId));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<ResourceLocation> trialId
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.optionalFieldOf("trial_id")
                    .forGetter(TriggerInstance::trialId)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(ResourceLocation trialId) {
            if (this.trialId.isEmpty()) return true;
            return this.trialId.get().equals(trialId);
        }
    }
}
