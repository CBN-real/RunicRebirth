package com.github.runicrebirth.api.events;

import com.github.runicrebirth.damage.SpellDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SpellDamageEvent extends LivingEvent implements ICancellableEvent {

    private float amount;
    private final SpellDamageSource source;

    public SpellDamageEvent(LivingEntity target, float amount, SpellDamageSource source) {
        super(target);
        this.amount = amount;
        this.source = source;
    }

    public float getAmount() { return amount; }
    public void setAmount(float v) { this.amount = v; }
    public SpellDamageSource getSource() { return source; }
}
