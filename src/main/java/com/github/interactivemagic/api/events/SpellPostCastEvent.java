package com.github.interactivemagic.api.events;

import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class SpellPostCastEvent extends EntityEvent {

    private final SpellCastContext ctx;
    private final SpellType type;
    private final SpellParams params;

    public SpellPostCastEvent(SpellCastContext ctx, SpellType type, SpellParams params) {
        super(ctx.caster());
        this.ctx = ctx;
        this.type = type;
        this.params = params;
    }

    public SpellCastContext ctx() { return ctx; }
    public SpellType type() { return type; }
    public SpellParams params() { return params; }
}
