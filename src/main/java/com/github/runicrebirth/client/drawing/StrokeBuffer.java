package com.github.runicrebirth.client.drawing;

import com.github.runicrebirth.magic.recognition.StrokePoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Accumulates strokes for one canvas session. Client-side only.
 * Each stroke = List of StrokePoint in canvas-local coordinates.
 */
public class StrokeBuffer {

    private final List<List<StrokePoint>> strokes = new ArrayList<>();
    private List<StrokePoint> current;

    public void beginStroke() {
        current = new ArrayList<>();
        strokes.add(current);
    }

    public void appendPoint(double x, double y, double minSeparation) {
        if (current == null) return;
        if (!current.isEmpty()) {
            StrokePoint last = current.get(current.size() - 1);
            double dx = x - last.x();
            double dy = y - last.y();
            if (dx * dx + dy * dy < minSeparation * minSeparation) return;
        }
        current.add(new StrokePoint(x, y));
    }

    public void endStroke() {
        if (current != null && current.isEmpty()) {
            strokes.remove(strokes.size() - 1);
        }
        current = null;
    }

    public boolean isEmpty() {
        for (List<StrokePoint> s : strokes) if (!s.isEmpty()) return false;
        return true;
    }

    public int totalPoints() {
        int n = 0;
        for (List<StrokePoint> s : strokes) n += s.size();
        return n;
    }

    public List<List<StrokePoint>> strokes() { return strokes; }

    public List<List<StrokePoint>> snapshot() {
        List<List<StrokePoint>> copy = new ArrayList<>(strokes.size());
        for (List<StrokePoint> s : strokes) copy.add(List.copyOf(s));
        return copy;
    }

    public void clear() {
        strokes.clear();
        current = null;
    }
}
