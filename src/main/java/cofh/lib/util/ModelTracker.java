package cofh.lib.util;

import cofh.core.util.helpers.RenderHelper;
import cofh.core.client.DummyVertexConsumer;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModelTracker {

    public static <T extends Entity> List<PartRecord> record(PoseStack stack, T entity, float partialTick) {

        BufferSource buffer = new BufferSource();
        RenderHelper.renderEntity().getRenderer(entity).render(entity, Mth.lerp(partialTick, entity.yRotO, entity.getYRot()), partialTick, stack, buffer, RenderHelper.FULL_BRIGHT);
        return buffer.parts;
    }

    public static <T extends Entity> List<PartRecord> record(T entity, float partialTick) {

        EntityRenderer<? super T> renderer = RenderHelper.renderEntity().getRenderer(entity);
        PoseStack stack = new PoseStack();
        Vec3 offset = renderer.getRenderOffset(entity, partialTick);
        stack.translate(
                Mth.lerp(partialTick, entity.xOld, entity.getX()) + offset.x,
                Mth.lerp(partialTick, entity.yOld, entity.getY()) + offset.y,
                Mth.lerp(partialTick, entity.zOld, entity.getZ()) + offset.z);
        return record(stack, entity, partialTick);
    }

    public record PartRecord(List<Quad> quads, @Nullable ResourceLocation texture) {

    }

    public record Quad(Vertex... vertices) {

        public float width() {

            return vertices[0].distance(vertices[1]);
        }

        public float height() {

            return vertices[0].distance(vertices[3]);
        }

        public Vector3f position(float u, float v) {

            return vertices[0].lerp(vertices[1], u).lerp(vertices[3].lerp(vertices[2], u), v);
        }

        public Color color(float u, float v) {

            return vertices[0].lerpColor(vertices[1], u).mix(vertices[3].lerpColor(vertices[2], u), v);
        }

    }

    public static class Vertex {

        public float x, y, z;
        public float u, v;
        public int r, g, b, a;

        public Vertex() {

            r = g = b = a = 255;
        }

        public float distance(Vertex other) {

            return MathHelper.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z));
        }

        public Vector3f lerp(Vertex other, float d) {

            return new Vector3f(x, y, z).lerp(new Vector3f(other.x, other.y, other.z), d);
        }

        public Vector2f lerpUV(Vertex other, float d) {

            return new Vector2f(u, v).lerp(new Vector2f(other.u, other.v), d);
        }

        public Color lerpColor(Vertex other, float d) {

            return Color.fromRGBA(r, g, b, a).mix(Color.fromRGBA(other.r, other.g, other.b, other.a), d);
        }

    }

    protected static class BufferSource implements MultiBufferSource {

        protected List<PartRecord> parts = new ArrayList<>();

        @Override
        public VertexConsumer getBuffer(RenderType type) {

            if (type.mode() != VertexFormat.Mode.QUADS) {
                return new DummyVertexConsumer();
            }
            ResourceLocation texture = null;
            if (type instanceof RenderType.CompositeRenderType composite) {
                texture = composite.state().textureState.cutoutTexture().orElse(null);
            }
            parts.add(new PartRecord(new ArrayList<>(), texture));
            return new DummyVertexConsumer() {

                Vertex[] vertices = fresh();
                int count = 0;

                @Override
                public VertexConsumer vertex(double x, double y, double z) {

                    vertices[count].x = (float) x;
                    vertices[count].y = (float) y;
                    vertices[count].z = (float) z;
                    return this;
                }

                @Override
                public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {

                    vertices[count].r = pRed;
                    vertices[count].g = pGreen;
                    vertices[count].b = pBlue;
                    vertices[count].a = pAlpha;
                    return this;
                }

                @Override
                public VertexConsumer uv(float pU, float pV) {

                    vertices[count].u = pU;
                    vertices[count].v = pV;
                    return this;
                }

                @Override
                public void endVertex() {

                    if (++count >= 4) {
                        count = 0;
                        parts.get(parts.size() - 1).quads.add(new Quad(vertices));
                        vertices = fresh();
                    }
                }

                Vertex[] fresh() {

                    return new Vertex[]{ new Vertex(), new Vertex(), new Vertex(), new Vertex() };
                }

            };
        }
    }

}
