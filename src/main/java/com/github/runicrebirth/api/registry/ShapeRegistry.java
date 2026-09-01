package com.github.runicrebirth.api.registry;

import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.magic.recognition.StrokePoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;

public final class ShapeRegistry {

    /** Shape definition. `threshold` is the minimum recognizer score for accepting a match;
     *  $P scores can dip negative for template/drawing mismatches (bounding-box, sample count),
     *  so each shape tunes its own floor individually. */
    public record Shape(Identifier id, List<StrokePoint> template, double threshold, Supplier<SpellComponent> component) {}

    private static final Map<Identifier, Shape> SHAPES = new HashMap<>();

    private ShapeRegistry() {}

    public static void register(Identifier id, List<StrokePoint> template, double threshold, Supplier<SpellComponent> component) {
        SHAPES.put(id, new Shape(id, template, threshold, component));
    }

    public static Shape get(Identifier id) {
        return SHAPES.get(id);
    }

    public static List<Shape> all() {
        return new ArrayList<>(SHAPES.values());
    }

    public static SpellComponent componentFor(Identifier id) {
        Shape s = SHAPES.get(id);
        return s == null ? null : s.component().get();
    }

    public static double thresholdFor(Identifier id) {
        Shape s = SHAPES.get(id);
        return s == null ? Double.NEGATIVE_INFINITY : s.threshold();
    }
}
