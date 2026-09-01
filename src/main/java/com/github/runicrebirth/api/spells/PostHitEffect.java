package com.github.runicrebirth.api.spells;

public record PostHitEffect(
    int fireTicks,
    int freezeTicks,
    float lifestealPercent
) {
    public static final PostHitEffect NONE = new PostHitEffect(0, 0, 0f);
}
