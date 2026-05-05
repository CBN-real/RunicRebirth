package com.github.interactivemagic.api.damage;

import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;

public interface ISpellDamageSource {
    MagicDamageType magicDamageType();
    Element element();
    float lifestealPercent();
    int fireTicks();
    int freezeTicks();
    int iFrames();
    boolean hasPostHitEffects();
}
