package com.github.runicrebirth.api.damage;

import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.damage.SpellDamageSource;
import net.minecraft.world.entity.Entity;

public final class SpellDamageBuilder {

    private final Entity direct;
    private final Entity causing;
    private final MagicDamageType damageType;
    private final Element element;
    private float lifesteal;
    private int fireTicks;
    private int freezeTicks;
    private int iFrames = -1;

    private SpellDamageBuilder(Entity direct, Entity causing, MagicDamageType damageType, Element element) {
        this.direct = direct;
        this.causing = causing;
        this.damageType = damageType;
        this.element = element;
    }

    public static SpellDamageBuilder from(Entity causing, MagicDamageType damageType, Element element) {
        return new SpellDamageBuilder(causing, causing, damageType, element);
    }

    public static SpellDamageBuilder from(Entity direct, Entity causing, MagicDamageType damageType, Element element) {
        return new SpellDamageBuilder(direct, causing, damageType, element);
    }

    public SpellDamageBuilder lifesteal(float percent) { this.lifesteal = percent; return this; }
    public SpellDamageBuilder fireTicks(int t) { this.fireTicks = t; return this; }
    public SpellDamageBuilder freezeTicks(int t) { this.freezeTicks = t; return this; }
    public SpellDamageBuilder iFrames(int t) { this.iFrames = t; return this; }

    public SpellDamageSource build() {
        SpellDamageSource s = SpellDamageSource.source(direct, causing, damageType, element);
        s.setLifestealPercent(lifesteal);
        s.setFireTicks(fireTicks);
        s.setFreezeTicks(freezeTicks);
        s.setIFrames(iFrames);
        return s;
    }
}
