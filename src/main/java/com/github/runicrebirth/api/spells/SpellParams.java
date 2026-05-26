package com.github.runicrebirth.api.spells;

import java.util.ArrayList;
import java.util.List;

public class SpellParams {

    public float damage;
    public float size;
    public float speed;
    public int duration;
    public int castingDelayTicks;
    public int piercing;
    public Element element;
    public MagicDamageType damageCategory;
    public final List<PostHitEffect> postHitEffects = new ArrayList<>();
    public final List<String> modifierIds = new ArrayList<>();

    public int extraCasts;
    public boolean useCharges;
    public float rangeMultiplier = 1.0f;
    public int cooldownOverrideTicks = -1;


    public SpellParams(float damage, float size, float speed, int duration, int castingDelayTicks, int piercing,
                       Element element, MagicDamageType damageCategory) {
        this.damage = damage;
        this.size = size;
        this.speed = speed;
        this.duration = duration;
        this.castingDelayTicks = castingDelayTicks;
        this.piercing = piercing;
        this.element = element;
        this.damageCategory = damageCategory;
    }

    public static SpellParams base(Element defaultElement, MagicDamageType damageCategory) {
        return new SpellParams(1f, 1f, 1f, 20, 15, 0, defaultElement, damageCategory);
    }

    public void addPostHitEffect(PostHitEffect effect) {
        this.postHitEffects.add(effect);
    }

    public SpellParams copy() {
        SpellParams c = new SpellParams(damage, size, speed, duration, piercing, castingDelayTicks, element, damageCategory);
        c.postHitEffects.addAll(this.postHitEffects);
        c.modifierIds.addAll(this.modifierIds);
        c.extraCasts = this.extraCasts;
        c.useCharges = this.useCharges;
        c.rangeMultiplier = this.rangeMultiplier;
        c.cooldownOverrideTicks = this.cooldownOverrideTicks;
        c.castingDelayTicks = this.castingDelayTicks;
        return c;
    }
}
