package com.github.runicrebirth.advancement.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class MagicKillTrigger extends SimpleCriterionTrigger<MagicKillTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation spellTypeId, double distance, ResourceLocation victimType) {
        this.trigger(player, instance -> instance.matches(spellTypeId, distance, victimType));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<ResourceLocation> spellType,
            Optional<Double> minDistance,
            Optional<Double> maxDistance,
            Optional<ResourceLocation> victimEntityType
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                    .forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.optionalFieldOf("spell_type")
                    .forGetter(TriggerInstance::spellType),
                Codec.DOUBLE.optionalFieldOf("min_distance")
                    .forGetter(TriggerInstance::minDistance),
                Codec.DOUBLE.optionalFieldOf("max_distance")
                    .forGetter(TriggerInstance::maxDistance),
                ResourceLocation.CODEC.optionalFieldOf("victim_entity_type")
                    .forGetter(TriggerInstance::victimEntityType)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(ResourceLocation killingSpellType, double dist, ResourceLocation victimType) {
            if (spellType.isPresent() && !spellType.get().equals(killingSpellType)) return false;
            if (minDistance.isPresent() && dist < minDistance.get()) return false;
            if (maxDistance.isPresent() && dist > maxDistance.get()) return false;
            if (victimEntityType.isPresent() && !victimEntityType.get().equals(victimType)) return false;
            return true;
        }
    }
}
