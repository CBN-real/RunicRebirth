package com.github.runicrebirth.magic.recognition;

import com.github.runicrebirth.api.registry.ShapeRegistry;
import java.util.ArrayList;
import java.util.List;

public final class Recognizers {

    private static ShapeRecognizer cached;

    private Recognizers() {}

    /** Rebuild from current ShapeRegistry contents. Call after initial registration. */
    public static void rebuild() {
        List<ShapeRecognizer.Template> templates = new ArrayList<>();
        for (ShapeRegistry.Shape shape : ShapeRegistry.all()) {
            templates.add(new ShapeRecognizer.Template(shape.id().toString(), shape.template()));
        }
        cached = new ShapeRecognizer(templates);
    }

    public static ShapeRecognizer get() {
        if (cached == null) rebuild();
        return cached;
    }
}
