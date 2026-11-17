package cofh.core.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class DummyVertexConsumer implements VertexConsumer {

    @Override
    public VertexConsumer vertex(double x, double y, double z) {

        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {

        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {

        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {

        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {

        return this;
    }

    @Override
    public void endVertex() {

    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {

    }

    @Override
    public void unsetDefaultColor() {

    }

}
