package com.github.runicrebirth.api.damage;

import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;

public interface ISpellDamageSource {
    MagicDamageType magicDamageType();
    Element element();
    float lifestealPercent();
    int fireTicks();
    int freezeTicks();
    int iFrames();
    boolean hasPostHitEffects();
}
