package com.github.interactivemagic.api.events;

import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class SpellPreCastEvent extends EntityEvent implements ICancellableEvent {

    private final SpellCastContext ctx;
    private final SpellType type;
    private final SpellParams params;

    public SpellPreCastEvent(SpellCastContext ctx, SpellType type, SpellParams params) {
        super(ctx.caster());
        this.ctx = ctx;
        this.type = type;
        this.params = params;
    }

    public SpellCastContext ctx() { return ctx; }
    public SpellType type() { return type; }
    public SpellParams params() { return params; }
}
