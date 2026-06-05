package com.github.runicrebirth.magic.recognition;

import java.util.ArrayList;
import java.util.List;

/**
 * $P Point-Cloud Recognizer (Vatavu, Anthony, Wobbrock — ICMI 2012).
 * Stroke-order/count-insensitive. No Minecraft deps.
 *
 * Pipeline: concat strokes → resample to N points → scale to unit square → translate centroid to origin
 * → greedy cloud distance vs each template → pick smallest.
 */
public final class ShapeRecognizer {

    private static final int N = 128;
    private static final double SCALE_SIZE = 250.0;
    private static final double HALF_DIAGONAL = 0.5 * Math.sqrt(SCALE_SIZE * SCALE_SIZE + SCALE_SIZE * SCALE_SIZE);

    public record Template(String id, List<StrokePoint> points) {}
    public record Result(String id, double score) {}

    private final List<Template> templates;

    public ShapeRecognizer(List<Template> rawTemplates) {
        List<Template> normalized = new ArrayList<>(rawTemplates.size());
        for (Template t : rawTemplates) {
            normalized.add(new Template(t.id(), normalize(t.points())));
        }
        this.templates = normalized;
    }

    /** Convenience: single-stroke input. */
    public Result recognize(List<StrokePoint> candidate) {
        return recognizeStrokes(List.of(candidate));
    }

    /** Primary entry: list of strokes, each a list of points. Strokes get concatenated into one cloud. */
    public Result recognizeStrokes(List<List<StrokePoint>> strokes) {
        if (strokes == null || strokes.isEmpty()) return null;
        List<StrokePoint> flat = new ArrayList<>();
        for (List<StrokePoint> s : strokes) {
            if (s != null) flat.addAll(s);
        }
        if (flat.size() < 2) return null;
        List<StrokePoint> points = normalize(flat);

        double bestDistance = Double.POSITIVE_INFINITY;
        String bestId = null;
        for (Template t : templates) {
            double d = greedyCloudMatch(points, t.points());
            if (d < bestDistance) {
                bestDistance = d;
                bestId = t.id();
            }
        }
        double score = 1.0 - bestDistance / HALF_DIAGONAL;
        return new Result(bestId, score);
    }

    private static List<StrokePoint> normalize(List<StrokePoint> raw) {
        List<StrokePoint> resampled = resample(raw, N);
        List<StrokePoint> scaled = scaleToSquare(resampled, SCALE_SIZE);
        return translateToOrigin(scaled);
    }

    private static List<StrokePoint> resample(List<StrokePoint> points, int n) {
        if (points.size() < 2) return new ArrayList<>(points);
        double interval = pathLength(points) / (n - 1);
        if (interval <= 0) {
            List<StrokePoint> out = new ArrayList<>(n);
            StrokePoint p = points.get(0);
            for (int i = 0; i < n; i++) out.add(p);
            return out;
        }
        double accumulated = 0.0;
        List<StrokePoint> work = new ArrayList<>(points);
        List<StrokePoint> out = new ArrayList<>(n);
        out.add(work.get(0));
        for (int i = 1; i < work.size(); i++) {
            StrokePoint prev = work.get(i - 1);
            StrokePoint curr = work.get(i);
            double d = prev.distance(curr);
            if ((accumulated + d) >= interval) {
                double t = (interval - accumulated) / d;
                double qx = prev.x() + t * (curr.x() - prev.x());
                double qy = prev.y() + t * (curr.y() - prev.y());
                StrokePoint q = new StrokePoint(qx, qy);
                out.add(q);
                work.add(i, q);
                accumulated = 0.0;
            } else {
                accumulated += d;
            }
        }
        while (out.size() < n) out.add(work.get(work.size() - 1));
        return out;
    }

    private static double pathLength(List<StrokePoint> pts) {
        double d = 0.0;
        for (int i = 1; i < pts.size(); i++) {
            d += pts.get(i - 1).distance(pts.get(i));
        }
        return d;
    }

    private static List<StrokePoint> scaleToSquare(List<StrokePoint> pts, double size) {
        double[] bbox = bbox(pts);
        double w = bbox[2] - bbox[0];
        double h = bbox[3] - bbox[1];
        double scale = Math.max(w, h);
        if (scale < 1.0E-6) return new ArrayList<>(pts);
        List<StrokePoint> out = new ArrayList<>(pts.size());
        for (StrokePoint p : pts) {
            double qx = (p.x() - bbox[0]) * (size / scale);
            double qy = (p.y() - bbox[1]) * (size / scale);
            out.add(new StrokePoint(qx, qy));
        }
        return out;
    }

    private static List<StrokePoint> translateToOrigin(List<StrokePoint> pts) {
        double sx = 0, sy = 0;
        for (StrokePoint p : pts) { sx += p.x(); sy += p.y(); }
        double cx = sx / pts.size(), cy = sy / pts.size();
        List<StrokePoint> out = new ArrayList<>(pts.size());
        for (StrokePoint p : pts) out.add(new StrokePoint(p.x() - cx, p.y() - cy));
        return out;
    }

    private static double[] bbox(List<StrokePoint> pts) {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (StrokePoint p : pts) {
            if (p.x() < minX) minX = p.x();
            if (p.y() < minY) minY = p.y();
            if (p.x() > maxX) maxX = p.x();
            if (p.y() > maxY) maxY = p.y();
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    // $P greedy cloud match — iterates start index with a step size to reduce complexity.
    private static double greedyCloudMatch(List<StrokePoint> a, List<StrokePoint> b) {
        double e = 0.50;
        int step = Math.max(1, (int) Math.floor(Math.pow(N, 1.0 - e)));
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < N; i += step) {
            double d1 = cloudDistance(a, b, i);
            double d2 = cloudDistance(b, a, i);
            double d = Math.min(d1, d2);
            if (d < min) min = d;
        }
        return min;
    }

    private static double cloudDistance(List<StrokePoint> a, List<StrokePoint> b, int start) {
        boolean[] matched = new boolean[N];
        double sum = 0;
        int i = start;
        do {
            double minD = Double.POSITIVE_INFINITY;
            int index = -1;
            for (int j = 0; j < N; j++) {
                if (!matched[j]) {
                    double d = a.get(i).distance(b.get(j));
                    if (d < minD) {
                        minD = d;
                        index = j;
                    }
                }
            }
            if (index < 0) break;
            matched[index] = true;
            double weight = 1.0 - ((i - start + N) % N) / (double) N;
            sum += weight * minD;
            i = (i + 1) % N;
        } while (i != start);
        return sum;
    }
}
