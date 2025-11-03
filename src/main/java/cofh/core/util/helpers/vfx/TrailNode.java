package cofh.core.util.helpers.vfx;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TrailNode {

    public final float xp, xn;
    public final float yp, yn;
    public final float z;

    public TrailNode(float xp, float xn, float yp, float yn, float z) {

        this.xp = xp;
        this.xn = xn;
        this.yp = yp;
        this.yn = yn;
        this.z = z;
    }

    public TrailNode(Vector4f pos, Vector2f perp) {

        this(pos.x + perp.x, pos.x - perp.x, pos.y + perp.y, pos.y - perp.y, pos.z);
    }

    public static TrailNode of(float xc, float xw, float yc, float yw, float z) {

        return new TrailNode(xc + xw, xc - xw, yc + yw, yc - yw, z);
    }

    public float xMid() {

        return (xp + xn) * 0.5F;
    }

    public float yMid() {

        return (yp + yn) * 0.5F;
    }

    public TrailNode renderStart(Vector3f normal, VertexConsumer builder, int light, int overlay, Color col, float u0, float v0, float v1) {

        builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(normal.x, normal.y, normal.z).endVertex();
        builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(normal.x, normal.y, normal.z).endVertex();
        return this;
    }

    public TrailNode renderStart(Vector3f normal, VertexConsumer builder, int light, Color color) {

        return renderStart(normal, builder, light, OverlayTexture.NO_OVERLAY, color, 0, 0, 1);
    }

    public TrailNode renderEnd(Vector3f normal, VertexConsumer builder, int light, int overlay, Color col, float u1, float v0, float v1) {

        builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal.x, normal.y, normal.z).endVertex();
        builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(normal.x, normal.y, normal.z).endVertex();
        return this;
    }

    public TrailNode renderEnd(Vector3f normal, VertexConsumer builder, int light, Color color) {

        return renderEnd(normal, builder, light, OverlayTexture.NO_OVERLAY, color, 1, 0, 1);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int light, int overlay, Color col, float u0, float v0, float v1, float u1, float v2, float v3) {

        renderEnd(normal, builder, light, overlay, col, u1, v2, v3);
        renderStart(normal, builder, light, overlay, col, u0, v0, v1);
        return this;
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int light, int overlay, Color col, float u0, float u1, float v0, float v1) {

        return renderMid(normal, builder, light, overlay, col, u0, v0, v1, u1, v0, v1);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int light, int overlay, Color col, float u, float v0, float v1) {

        return renderMid(normal, builder, light, overlay, col, u, v0, v1, u, v0, v1);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int light, Color col) {

        return renderMid(normal, builder, light, OverlayTexture.NO_OVERLAY, col, 0, 1, 0, 1);
    }

    @Override
    public String toString() {

        return "{" + xp + ", " + xn + "}, {" + yp + ", " + yn + "}, " + z;
    }

}
