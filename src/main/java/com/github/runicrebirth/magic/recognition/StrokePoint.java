package com.github.runicrebirth.magic.recognition;

public record StrokePoint(double x, double y) {
    public StrokePoint add(StrokePoint other) {
        return new StrokePoint(this.x + other.x, this.y + other.y);
    }

    public StrokePoint sub(StrokePoint other) {
        return new StrokePoint(this.x - other.x, this.y - other.y);
    }

    public StrokePoint scale(double s) {
        return new StrokePoint(this.x * s, this.y * s);
    }

    public double distance(StrokePoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
