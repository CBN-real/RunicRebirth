package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class MagicKillTrigger extends SimpleCriterionTrigger<MagicKillTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Identifier spellTypeId, double distance, Identifier victimType) {
        this.trigger(player, instance -> instance.matches(spellTypeId, distance, victimType));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<Identifier> spellType,
            Optional<Double> minDistance,
            Optional<Double> maxDistance,
            Optional<Identifier> victimEntityType
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                // TODO: verify EntityPredicate.ADVANCEMENT_CODEC still exists in 26.1.2 (may have been renamed/removed)
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                Identifier.CODEC.optionalFieldOf("spell_type")
                    .forGetter(TriggerInstance::spellType),
                Codec.DOUBLE.optionalFieldOf("min_distance")
                    .forGetter(TriggerInstance::minDistance),
                Codec.DOUBLE.optionalFieldOf("max_distance")
                    .forGetter(TriggerInstance::maxDistance),
                Identifier.CODEC.optionalFieldOf("victim_entity_type")
                    .forGetter(TriggerInstance::victimEntityType)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(Identifier killingSpellType, double dist, Identifier victimType) {
            if (spellType.isPresent() && !spellType.get().equals(killingSpellType)) return false;
            if (minDistance.isPresent() && dist < minDistance.get()) return false;
            if (maxDistance.isPresent() && dist > maxDistance.get()) return false;
            if (victimEntityType.isPresent() && !victimEntityType.get().equals(victimType)) return false;
            return true;
        }
    }
}
