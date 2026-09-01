package com.github.runicrebirth.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class NormalOverrideVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;

    public NormalOverrideVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        delegate.setColor(r, g, b, a);
        return this;
    }

    @Override
    public VertexConsumer setColor(int color) {
        delegate.setColor(color);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(0, 1, 0);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float lineWidth) {
        return delegate.setLineWidth(lineWidth);
    }
}
