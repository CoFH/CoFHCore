package cofh.lib.util;

import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class ModelTracker {

    public static final VertexConsumer DUMMY_CONSUMER = new VertexConsumer() {

        @Override
        public VertexConsumer vertex(double pX, double pY, double pZ) {

            return this;
        }

        @Override
        public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {

            return this;
        }

        @Override
        public VertexConsumer uv(float pU, float pV) {

            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int pU, int pV) {

            return this;
        }

        @Override
        public VertexConsumer uv2(int pU, int pV) {

            return this;
        }

        @Override
        public VertexConsumer normal(float pX, float pY, float pZ) {

            return this;
        }

        @Override
        public void endVertex() {

        }

        @Override
        public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {

        }

        @Override
        public void unsetDefaultColor() {

        }

    };
    public static final MultiBufferSource DUMMY_BUFFER = type -> DUMMY_CONSUMER;

    public static boolean record = false;
    public static List<PartRecord> parts = new ArrayList<>();

    public static <T extends Entity> List<PartRecord> recordParts(T entity) {

        record = true;
        parts.clear();
        float partialTick = RenderHelper.partialTick();
        EntityRenderer<? super T> renderer = RenderHelper.renderEntity().getRenderer(entity);
        PoseStack stack = new PoseStack();
        Vec3 offset = renderer.getRenderOffset(entity, partialTick);
        stack.translate(
                Mth.lerp(partialTick, entity.xOld, entity.getX()) + offset.x,
                Mth.lerp(partialTick, entity.yOld, entity.getY()) + offset.y,
                Mth.lerp(partialTick, entity.zOld, entity.getZ()) + offset.z);
        renderer.render(entity, Mth.lerp(partialTick, entity.yRotO, entity.getYRot()), partialTick, stack, DUMMY_BUFFER, RenderHelper.FULL_BRIGHT);
        record = false;
        return parts;
    }

    public record PartRecord(List<ModelPart.Cube> cubes, Matrix4f pose) {

    }

}
