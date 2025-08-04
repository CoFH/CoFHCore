package cofh.core.util.helpers.vfx;

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

    public TrailNode renderStart(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float u1, float v0) {

        builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
        builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
        return this;
    }

    public TrailNode renderStart(Vector3f normal, VertexConsumer builder, int packedLight, Color color) {

        return renderStart(normal, builder, packedLight, color, 0, 1, 0);
    }

    public TrailNode renderEnd(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float u1, float v1) {

        builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
        builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
        return this;
    }

    public TrailNode renderEnd(Vector3f normal, VertexConsumer builder, int packedLight, Color color) {

        return renderEnd(normal, builder, packedLight, color, 0, 1, 1);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float u1, float v0, float u2, float u3, float v1) {

        renderEnd(normal, builder, packedLight, col, u2, u3, v1);
        renderStart(normal, builder, packedLight, col, u0, u1, v0);
        return this;
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float u1, float v0, float v1) {

        return renderMid(normal, builder, packedLight, col, u0, u1, v0, u0, u1, v1);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float u1, float v) {

        return renderMid(normal, builder, packedLight, col, u0, u1, v, u0, u1, v);
    }

    public TrailNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col) {

        return renderMid(normal, builder, packedLight, col, 0, 1, 0, 1);
    }

    @Override
    public String toString() {

        return "{" + xp + ", " + xn + "}, {" + yp + ", " + yn + "}, " + z;
    }

}
