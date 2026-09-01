package com.github.runicrebirth.api.events;

import com.github.runicrebirth.api.spells.SpellComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class ShapeRecognizedEvent extends EntityEvent {

    private final Identifier shapeId;
    private final double score;
    private final SpellComponent component;

    public ShapeRecognizedEvent(ServerPlayer player, Identifier shapeId, double score, SpellComponent component) {
        super(player);
        this.shapeId = shapeId;
        this.score = score;
        this.component = component;
    }

    public Identifier shapeId() { return shapeId; }
    public double score() { return score; }
    public SpellComponent component() { return component; }
}
