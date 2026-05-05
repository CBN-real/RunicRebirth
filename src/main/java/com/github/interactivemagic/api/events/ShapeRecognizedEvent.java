package com.github.interactivemagic.api.events;

import com.github.interactivemagic.api.spells.SpellComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class ShapeRecognizedEvent extends EntityEvent {

    private final ResourceLocation shapeId;
    private final double score;
    private final SpellComponent component;

    public ShapeRecognizedEvent(ServerPlayer player, ResourceLocation shapeId, double score, SpellComponent component) {
        super(player);
        this.shapeId = shapeId;
        this.score = score;
        this.component = component;
    }

    public ResourceLocation shapeId() { return shapeId; }
    public double score() { return score; }
    public SpellComponent component() { return component; }
}
