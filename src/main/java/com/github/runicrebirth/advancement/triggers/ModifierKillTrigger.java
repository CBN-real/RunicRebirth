package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ModifierKillTrigger extends SimpleCriterionTrigger<ModifierKillTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, String modifierPath, int totalKills) {
        this.trigger(player, instance -> instance.matches(modifierPath, totalKills));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<String> modifierPath,
            Optional<Integer> minCount
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                Codec.STRING.optionalFieldOf("modifier_path")
                    .forGetter(TriggerInstance::modifierPath),
                Codec.INT.optionalFieldOf("min_count")
                    .forGetter(TriggerInstance::minCount)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(String modifierPath, int totalKills) {
            if (this.modifierPath.isPresent() && !this.modifierPath.get().equals(modifierPath)) return false;
            return totalKills >= minCount.orElse(0);
        }
    }
}
