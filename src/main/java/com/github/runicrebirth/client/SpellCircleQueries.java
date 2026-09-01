package com.github.runicrebirth.client;

import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.entities.spells.AbstractSpellCircleEntity;
import com.geckolib.loading.math.MolangQueries;

public final class SpellCircleQueries {

    private SpellCircleQueries() {}

    public static void register() {
        registerTypeQuery("query.is_projectile", "magic_projectile");
        registerTypeQuery("query.is_beam", "magic_beam");
        registerTypeQuery("query.is_blast", "magic_blast");
        registerTypeQuery("query.is_arrow", "magic_arrow");
        registerTypeQuery("query.is_explosion", "magic_explosion");
        registerTypeQuery("query.is_slash", "magic_slash");
        registerTypeQuery("query.is_meteor", "magic_meteor");
        registerTypeQuery("query.is_shield", "magic_shield");
        registerTypeQuery("query.is_hammer", "magic_hammer");
        registerTypeQuery("query.is_binding", "magic_binding");
        registerTypeQuery("query.is_ballista", "magic_ballista");

        MolangQueries.<Object>setActorVariable("query.is_plus", actor -> {
            if (actor.animatable() instanceof AbstractCircleEntity circle) {
                if (circle.hasModifier("size_plus_four")) return 3.0;
                if (circle.hasModifier("size_plus_two")) return 2.0;
                if (circle.hasModifier("size_plus")) return 1.5;
                return 1.0;
            }
            if (actor.animatable() instanceof AbstractSpellCircleEntity circle) {
                if (circle.hasModifier("size_plus_four")) return 3.0;
                if (circle.hasModifier("size_plus_two")) return 2.0;
                if (circle.hasModifier("size_plus")) return 1.5;
                return 1.0;
            }
            return 1.0;
        });

        registerModifierQuery("query.is_additive_size", "additive_size");
        registerModifierQuery("query.is_range", "range");
        registerModifierQuery("query.is_cooldown", "cooldown");
        registerModifierQuery("query.is_charges", "charges");

        MolangQueries.<Object>setActorVariable("query.is_intermediate_circle", actor ->
            actor.animatable() instanceof IntermediateCircleEntity ? 1 : 0);
        MolangQueries.<Object>setActorVariable("query.is_advanced_circle", actor ->
            actor.animatable() instanceof AdvancedCircleEntity ? 1 : 0);

        MolangQueries.<Object>setActorVariable("query.is_multicast", actor -> {
            if (!(actor.animatable() instanceof AbstractCircleEntity circle)) return 0;
            if (circle.hasModifier("four_casts")) return 4.0;
            if (circle.hasModifier("two_casts")) return 2.0;
            return 0;
        });
    }

    private static void registerTypeQuery(String queryName, String typeId) {
        MolangQueries.<Object>setActorVariable(queryName, actor ->
            actor.animatable() instanceof AbstractCircleEntity circle
                && circle.getSpellTypeId().equals(typeId) ? 1 : 0);
    }

    private static void registerModifierQuery(String queryName, String modifierId) {
        MolangQueries.<Object>setActorVariable(queryName, actor ->
            actor.animatable() instanceof AbstractCircleEntity circle
                && circle.hasModifier(modifierId) ? 1 : 0);
    }
}
