package cofh.core.util.helpers.vfx;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.floats.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.*;

import java.lang.Math;
import java.util.Random;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import static cofh.core.util.helpers.vfx.RenderTypes.*;

/**
 * The cooler version of RenderHelper.
 *
 * @author Hekera
 */
public final class VFXHelper {

    public static float lengthSqr(Vector3f vec) {

        return MathHelper.distSqr(vec.x(), vec.y(), vec.z());
    }

    public static float length(Vector3f vec) {

        return MathHelper.dist(vec.x(), vec.y(), vec.z());
    }

    public static VFXNode interpolate(VFXNode a, VFXNode b, float d) {

        return new VFXNode(MathHelper.interpolate(a.xp, b.xp, d), MathHelper.interpolate(a.xn, b.xn, d), MathHelper.interpolate(a.yp, b.yp, d), MathHelper.interpolate(a.yn, b.yn, d), MathHelper.interpolate(a.z, b.z, d), MathHelper.interpolate(a.width, b.width, d));
    }

    public static Vector4f interpolate(Vector4f a, Vector4f b, float d) {

        return new Vector4f(MathHelper.interpolate(a.x(), b.x(), d), MathHelper.interpolate(a.y(), b.y(), d), MathHelper.interpolate(a.z(), b.z(), d), MathHelper.interpolate(a.w(), b.w(), d));
    }

    private static VFXNode interpolateCap(VFXNode a, VFXNode b) {

        return interpolate(a, b, 1.0F + b.width * 0.5F / MathHelper.dist(a.xMid() - b.xMid(), a.yMid() - b.yMid(), a.z - b.z)); // TODO
    }

    public static Vector4f subtract(Vector4f a, Vector4f b) {

        return new Vector4f(a.x() - b.x(), a.y() - b.y(), a.z() - b.z(), a.w() - b.w());
    }

    public static Vector3f normal(Matrix3f transform) {

        return new Vector3f(0, 1, 0).mul(transform);
    }

    public static Vector3f normal(PoseStack stack) {

        return normal(stack.last().normal());
    }

    //public static int mix(float d, int rgba0, int... colors) {
    //
    //    if (colors.length <= 0 || d <= 0) {
    //        return rgba0;
    //    }
    //    if (d >= 1) {
    //        return colors[colors.length - 1];
    //    }
    //    int i = MathHelper.floor(d * colors.length);
    //    int x = i <= 0 ? rgba0 : colors[i - 1];
    //    int y = colors[i];
    //    d = d * colors.length - i;
    //    int r = MathHelper.interpolate((x >> 24) & 0xFF, (y >> 24) & 0xFF, d);
    //    int g = MathHelper.interpolate((x >> 16) & 0xFF, (y >> 16) & 0xFF, d);
    //    int b = MathHelper.interpolate((x >> 8) & 0xFF, (y >> 8) & 0xFF, d);
    //    int a = MathHelper.interpolate(x & 0xFF, y & 0xFF, d);
    //    return packRGBA(r, g, b, a);
    //}

    public static Vector4f mid(Vector4f a, Vector4f b) {

        return new Vector4f((a.x() + b.x()) * 0.5F, (a.y() + b.y()) * 0.5F, (a.z() + b.z()) * 0.5F, (a.w() + b.w()) * 0.5F);
    }

    // region HELPERS
    public static void renderNodes(Vector3f normal, VertexConsumer builder, int packedLight, VFXNode[] nodes, Color color) {

        nodes[0].renderStart(normal, builder, packedLight, color);
        int count = nodes.length - 1;
        for (int i = 1; i < count; ++i) {
            nodes[i].renderMid(normal, builder, packedLight, color);
        }
        nodes[count].renderEnd(normal, builder, packedLight, color);
    }

    public static void renderNodesCapped(Vector3f normal, MultiBufferSource buffer, RenderType midType, RenderType capType, int packedLight, VFXNode[] nodes, Color color) {

        if (nodes.length < 2) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(midType);
        renderNodes(normal, consumer, packedLight, nodes, color);

        consumer = buffer.getBuffer(capType);
        interpolateCap(nodes[1], nodes[0]).renderStart(normal, consumer, packedLight, color, 0, 0, 1.0F, 0.5F);
        nodes[0].renderEnd(normal, consumer, packedLight, color, 0, 0, 1.0F, 0.5F);
        nodes[nodes.length - 1].renderStart(normal, consumer, packedLight, color, 0, 0.5F, 1.0F, 1.0F);
        interpolateCap(nodes[nodes.length - 2], nodes[nodes.length - 1]).renderEnd(normal, consumer, packedLight, color, 0, 0.5F, 1.0F, 1.0F);
    }

    public static Vector2f axialPerp(Vector4f start, Vector4f end, float width) {

        float x = -start.x();
        float y = -start.y();
        if (Math.abs(start.z()) > 0) {
            float ratio = end.z() / start.z();
            x = end.x() + x * ratio;
            y = end.y() + y * ratio;
        } else if (Math.abs(end.z()) <= 0) {
            x += end.x();
            y += end.y();
        }
        if (start.z() > 0) {
            x = -x;
            y = -y;
        }
        if (x * x + y * y > 0F) {
            float normalize = width * 0.5F / MathHelper.dist(x, y);
            x *= normalize;
            y *= normalize;
        }
        return new Vector2f(-y, x);
    }

    public static Vector2f axialPerp(Vector3f start, Vector3f end, float width) {

        float x = -start.x();
        float y = -start.y();
        if (Math.abs(start.z()) > 0) {
            float ratio = end.z() / start.z();
            x = end.x() + x * ratio;
            y = end.y() + y * ratio;
        } else if (Math.abs(end.z()) <= 0) {
            x += end.x();
            y += end.y();
        }
        if (start.z() > 0) {
            x = -x;
            y = -y;
        }
        if (x * x + y * y > 0F) {
            float normalize = width * 0.5F / MathHelper.dist(x, y);
            x *= normalize;
            y *= normalize;
        }
        return new Vector2f(-y, x);
    }

    //public static VFXNode axialPerp(Matrix4f pose, Vector3f start, Vector3f end, float width) {
    //
    //    Vector4f s = new Vector4f(start);
    //    s.transform(pose);
    //    Vector4f e = new Vector4f(end);
    //    e.transform(pose);
    //    float ratio = end.z() / start.z();
    //    float x = -(end.y() - start.y() * ratio);
    //    float y = (end.x() - start.x() * ratio);
    //    float normalize = MathHelper.invDist(x, y);
    //    x *= normalize;
    //    y *= normalize;
    //    return new VFXNode();
    //}

    //public static VFXNode axialPerp(PoseStack stack, Vector3f axisStart, Vector3f axisEnd, Vector3f pos, float width) {
    //
    //    Matrix3f normal = stack.last().normal();
    //    Matrix4f pose = stack.last().pose();
    //    Vector4f s = new Vector4f(start);
    //    s.transform(pose);
    //    Vector4f e = new Vector4f(end);
    //    e.transform(pose);
    //    float ratio = end.z() / start.z();
    //    float x = -(end.y() - start.y() * ratio);
    //    float y = (end.x() - start.x() * ratio);
    //    float normalize = MathHelper.invDist(x, y);
    //    x *= normalize;
    //    y *= normalize;
    //    return new VFXNode();
    //}

    /**
     * Returns a quaternion that transforms a matrix stack such that YP points in the given direction.
     * XP, YP, and ZP remain orthogonal to each other.
     */
    public static Quaternionf alignVertical(Vector3f dir) {

        if (dir.x() == 0 && dir.y() == 0 && dir.z() == 0) {
            return new Quaternionf();
        }
        return MathHelper.rotation(new Vector3f(dir).normalize().add(0, 1, 0).normalize(), MathHelper.F_PI);
    }

    /**
     * Transforms a matrix stack such that YP points in the given direction.
     * XP, YP, and ZP remain orthogonal to each other.
     */
    public static void alignVertical(PoseStack stack, Vector3f dir) {

        stack.mulPose(alignVertical(dir));
    }

    /**
     * Transforms a matrix stack such that YP starts and ends at the given positions.
     * XP, YP, and ZP remain orthogonal to each other. XP and ZP are scaled the same amount as YP.
     */
    public static void alignVertical(PoseStack stack, Vector3f start, Vector3f end) {

        Vector3f diff = new Vector3f(end);
        diff.sub(start);
        float scale = length(diff);
        stack.translate(start.x(), start.y(), start.z());
        alignVertical(stack, diff);
        stack.scale(scale, scale, scale);
    }
    // endregion

    // region DEBUG

    /**
     * Renders a billboarded translucent magenta square. Useful for preventing insanity.
     */
    public static void renderTest(PoseStack stack, VertexConsumer consumer) {

        Vector4f center = new Vector4f(0, 0, 0, 1).mul(stack.last().pose());
        Matrix3f normal = stack.last().normal();
        float xp = center.x() + 0.5F;
        float xn = center.x() - 0.5F;
        float yp = center.y() + 0.5F;
        float yn = center.y() - 0.5F;
        float z = center.z();
        int r = 255;
        int g = 0;
        int b = 255;
        int a = 255;
        int packedLight = 0x00F000F0;
        int overlay = OverlayTexture.pack(0, false); //OverlayTexture.NO_OVERLAY
        consumer.vertex(xp, yp, z).color(r, g, b, a).uv(0, 0).overlayCoords(overlay).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(xn, yp, z).color(r, g, b, a).uv(0, 1).overlayCoords(overlay).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(xn, yn, z).color(r, g, b, a).uv(1, 1).overlayCoords(overlay).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(xp, yn, z).color(r, g, b, a).uv(1, 0).overlayCoords(overlay).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    public static void renderTest(PoseStack stack, MultiBufferSource buffer) {

        renderTest(stack, buffer.getBuffer(FLAT_TRANSLUCENT));
    }

    public static void renderTest(PoseStack stack) {

        renderTest(stack, Minecraft.getInstance().renderBuffers().bufferSource());
    }

    private static void renderSkeleton(VertexConsumer builder, VFXNode[] nodes) {

        VFXNode node = nodes[0];
        float xm = node.xMid();
        float ym = node.yMid();

        builder.vertex(node.xp, node.yp, node.z).color(255, 0, 0, 255).endVertex();
        builder.vertex(xm, ym, node.z).color(255, 0, 0, 255).endVertex();
        builder.vertex(xm, ym, node.z).color(0, 0, 255, 255).endVertex();
        builder.vertex(node.xn, node.yn, node.z).color(0, 0, 255, 255).endVertex();
        builder.vertex(xm, ym, node.z).color(255, 255, 255, 255).endVertex();
        for (int i = 1; i < nodes.length - 1; ++i) {
            node = nodes[i];
            xm = node.xMid();
            ym = node.yMid();
            builder.vertex(xm, ym, node.z).color(255, 255, 255, 255).endVertex();
            builder.vertex(node.xp, node.yp, node.z).color(255, 0, 0, 255).endVertex();
            builder.vertex(xm, ym, node.z).color(255, 0, 0, 255).endVertex();
            builder.vertex(xm, ym, node.z).color(0, 0, 255, 255).endVertex();
            builder.vertex(node.xn, node.yn, node.z).color(0, 0, 255, 255).endVertex();
            builder.vertex(xm, ym, node.z).color(255, 255, 255, 255).endVertex();
        }
        node = nodes[nodes.length - 1];
        xm = node.xMid();
        ym = node.yMid();
        builder.vertex(xm, ym, node.z).color(255, 255, 255, 255).endVertex();
        builder.vertex(node.xp, node.yp, node.z).color(255, 0, 0, 255).endVertex();
        builder.vertex(xm, ym, node.z).color(255, 0, 0, 255).endVertex();
        builder.vertex(xm, ym, node.z).color(0, 0, 255, 255).endVertex();
        builder.vertex(node.xn, node.yn, node.z).color(0, 0, 255, 255).endVertex();
    }

    private static void renderSkeleton(VFXNode[] nodes) {

        renderSkeleton(Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES), nodes);
    }
    // endregion

    // region SHOCKWAVE
    public static final Float2ReferenceSortedMap<Vector2i[]> SHOCKWAVE_OFFSETS = getOffsets(16);

    /**
     * Renders a block shockwave that radially propagates from the origin.
     *
     * @param origin      Center of the shockwave.
     * @param time        Travels outward 1 block per unit time. Blocks take 5 units to complete their trajectory.
     *                    Scale this value based on how fast you want the animation to play.
     * @param radius      The maximum radius of the shockwave. Hard limit of 16.
     * @param heightScale Adjusts how high the blocks travel.
     * @param canRender   Predicate for filtering which blocks are to be rendered.
     */
    public static void renderShockwave(PoseStack stack, MultiBufferSource buffer, Level level, BlockPos origin, float time, float radius, float heightScale, BiPredicate<BlockPos, BlockState> canRender) {

        BlockRenderDispatcher renderer = RenderHelper.renderBlock();
        float invRadius = 1 / radius;
        for (Float2ReferenceMap.Entry<Vector2i[]> entry : SHOCKWAVE_OFFSETS.subMap(Math.min(time - 5, radius), Math.min(time, radius)).float2ReferenceEntrySet()) {
            float dist = entry.getFloatKey();
            float progress = time - dist;
            double height = heightScale * 0.16 * (radius - dist * 0.5F) * progress * (5 - progress) * invRadius / Math.max(dist * 0.25F, 1.0F);
            for (Vector2i offset : entry.getValue()) {
                for (int y = 1; y >= -1; --y) {
                    BlockPos pos = origin.offset(offset.x, y, offset.y);
                    BlockState state = level.getBlockState(pos);
                    if (canRender.test(pos, state)) {
                        if (state.getRenderShape() == RenderShape.MODEL) {
                            stack.pushPose();
                            stack.translate(offset.x, height + y, offset.y);
                            stack.scale(1.01F, 1.01F, 1.01F);
                            // ModelData modelData = renderer.getBlockModel(state).getModelData(level, pos, state, ModelData.EMPTY);
                            for (RenderType type : renderer.getBlockModel(state).getRenderTypes(state, level.random, ModelData.EMPTY)) {
                                renderer.renderBatched(state, pos.relative(Direction.UP), level, stack, buffer.getBuffer(type), false, level.random, ModelData.EMPTY, type);
                            }
                            stack.popPose();
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void renderShockwave(PoseStack stack, MultiBufferSource buffer, Level world, BlockPos origin, float time, float radius, float heightScale) {

        renderShockwave(stack, buffer, world, origin, time, radius, heightScale, (pos, state) ->
                !state.isAir() && state.isRedstoneConductor(world, pos) &&
                        state.isCollisionShapeFullBlock(world, pos) && !state.hasBlockEntity() &&
                        !world.getBlockState(pos.above()).isCollisionShapeFullBlock(world, pos.above()));
    }

    private static Float2ReferenceSortedMap<Vector2i[]> getOffsets(int maxRadius) {

        Float2ReferenceMap<List<Vector2i>> blocks = new Float2ReferenceOpenHashMap<>();
        float maxSqr = maxRadius * maxRadius;
        for (int x = -maxRadius; x <= maxRadius; ++x) {
            for (int z = -maxRadius; z <= maxRadius; ++z) {
                int distSqr = x * x + z * z;
                if (distSqr < maxSqr) {
                    float dist = MathHelper.sqrt(distSqr);
                    blocks.computeIfAbsent(dist, d -> new ArrayList<>()).add(new Vector2i(x, z));
                }
            }
        }
        Float2ReferenceSortedMap<Vector2i[]> out = new Float2ReferenceAVLTreeMap<>();
        blocks.float2ReferenceEntrySet().forEach(entry -> out.put(entry.getFloatKey(), entry.getValue().toArray(Vector2i[]::new)));
        return Float2ReferenceSortedMaps.unmodifiable(out);
    }
    // endregion

    // region ELECTRICITY
    private static final Vector3f[][] arcs = getRandomArcs(new Random(), 8, 48);

    /**
     * Renders straight electric arcs in a unit column towards positive y.
     *
     * @param arcCount    Number of individual arcs.
     * @param arcWidth    Average width of each arc. 0.4F recommended.
     * @param widthVar    Absolute variance in the width of the arc.
     * @param seed        Seed for randomization. Should be changed based on the time.
     * @param coreColor   Color/alpha for the center part of the arc.
     * @param glowColor   Color/alpha for the glow surrounding the core.
     * @param taperOffset Value between -1.25F and 1.25F that determines the threshold for tapering.
     *                    Generally, negative at the start of an animation, 0 in the middle (no taper), and positive at the end.
     */
    public static void renderStraightArcs(PoseStack stack, MultiBufferSource buffer, int packedLight, int arcCount, float arcWidth, float widthVar, long seed, Color coreColor, Color glowColor, float taperOffset) {

        SplittableRandom rand = new SplittableRandom(seed);

        int nodeCount = arcs[0].length;
        int first = MathHelper.clamp((int) (nodeCount * (taperOffset - 0.25F) + 1), 0, nodeCount);
        int last = MathHelper.clamp((int) (nodeCount * (1.25F + taperOffset) + 1), 0, nodeCount);

        if (last - first <= 1) {
            return;
        }
        stack.pushPose();
        Matrix4f pose = stack.last().pose();
        Vector3f normal = normal(stack);
        Vector2f perp = axialPerp(new Vector4f(0, 0, 0, 1).mul(pose), new Vector4f(0, 1, 0, 1).mul(pose), 1.0F);

        //These are calculated first so they are not affected by differing taper values.
        Vector3f[][] randomArcs = new Vector3f[nodeCount][arcCount];
        float[] rotations = new float[arcCount];
        for (int i = 0; i < arcCount; ++i) {
            randomArcs[i] = arcs[rand.nextInt(arcs.length)];
            rotations[i] = (float) rand.nextDouble(360.0F);
        }

        float incr = 1.0F / nodeCount;
        for (int i = 0; i < arcCount; ++i) {
            stack.mulPose(Axis.YP.rotationDegrees(rotations[i]));
            Vector3f[] arc = randomArcs[i];
            VFXNode[] outer = new VFXNode[last - first];
            VFXNode[] inner = new VFXNode[last - first];
            for (int j = first; j < last; ++j) {
                Vector4f center = new Vector4f(0, arc[j].y(), 0, 1.0F).mul(pose);
                Vector4f pos = MathHelper.toVector4f(arc[j]).mul(pose);
                float dot = subtract(pos, center).dot(new Vector4f(perp.x, perp.y, 0, 0));
                float xc = center.x() + perp.x * dot * 3.0F;
                float yc = center.y() + perp.y * dot * 3.0F;
                float width = Math.max(arcWidth + (float) rand.nextDouble(-1.0F, 1.0F) * widthVar, 0) * MathHelper.clamp(4.0F * (0.75F - Math.abs(j * incr - 0.5F - taperOffset)), 0.0F, 1.0F);
                float xw = perp.x * width;
                float yw = perp.y * width;
                inner[j - first] = new VFXNode(xc + xw, xc - xw, yc + yw, yc - yw, center.z(), width);
                width = Math.max(width, arcWidth);
                xw += perp.x * width * 1.5F;
                yw += perp.y * width * 1.5F;
                outer[j - first] = new VFXNode(xc + xw, xc - xw, yc + yw, yc - yw, center.z(), width);
            }
            renderNodesCapped(normal, buffer, LINEAR_GLOW, ROUND_GLOW, packedLight, outer, glowColor);
            renderNodesCapped(normal, buffer, LINEAR_GLOW, ROUND_GLOW, packedLight, inner, coreColor);
        }
        stack.popPose();
    }

    public static void renderStraightArcs(PoseStack stack, MultiBufferSource buffer, int packedLightIn, int arcCount, float arcWidth, long seed, Color coreColor, Color glowColor, float taperOffset) {

        renderStraightArcs(stack, buffer, packedLightIn, arcCount, arcWidth, 0.3F * arcWidth, seed, coreColor, glowColor, taperOffset);
    }

    public static void renderStraightArcs(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float length, int arcCount, float arcWidth, long seed, Color coreColor, Color glowColor, float taperOffset) {

        stack.pushPose();
        stack.scale(length, length, length);
        renderStraightArcs(stack, buffer, packedLightIn, arcCount, arcWidth / Math.abs(length), seed, coreColor, glowColor, taperOffset);
        stack.popPose();
    }

    public static long getSeedWithTime(long seed, float time, float flickerRate) {

        return seed + 69420 * (int) (time * flickerRate);
    }

    public static long getSeedWithTime(long seed, float time) {

        return getSeedWithTime(seed, time, 0.75F);
    }

    public static float getTaperOffsetFromTimes(float time, float endTime, float taperTime) {

        float offset = 0.0F;
        if (time < taperTime) {
            offset = 1.25F * (time - taperTime) / taperTime;
        } else if (endTime - time < taperTime) {
            offset = 1.25F * (time + taperTime - endTime) / taperTime;
        }
        return offset;
    }

    public static float getTaperOffsetFromTimes(float time, float startTime, float endTime, float taperTime) {

        return getTaperOffsetFromTimes(time - startTime, endTime - startTime, taperTime);
    }

    private static Vector3f[][] getRandomArcs(Random random, int arcCount, int nodeCount) {

        Vector3f[][] arcs = new Vector3f[arcCount][nodeCount];
        for (int i = 0; i < arcs.length; ++i) {
            arcs[i] = getRandomNodes(random, nodeCount);
        }
        return arcs;
    }

    private static Vector3f[] getRandomNodes(Random random, int count) {

        SortedSet<Float> ySet = new TreeSet<>();
        float e = 0.25F / count;
        ySet.add(0.0F);
        ySet.add(1.0F);
        count -= 2;
        for (int i = 0; i < count * 3 && ySet.size() < count; ++i) {
            float next = random.nextFloat();
            if (ySet.subSet(next - e, next + e).size() <= 0) {
                ySet.add(next);
            }
        }
        for (int i = count - ySet.size(); i > 0; --i) {
            ySet.add(random.nextFloat());
        }

        Float[] y = ySet.toArray(Float[]::new);
        Vector3f[] nodes = new Vector3f[y.length];

        nodes[0] = new Vector3f(0, 0, 0);
        nodes[nodes.length - 1] = new Vector3f(0, 1, 0);

        for (int i = 1; i < nodes.length - 1; ++i) {
            float eccentricity = 0.3F * (y[i] - y[i - 1]);
            float centering = Math.min(1, 3.0F - 3.0F * i / nodes.length);
            nodes[i] = new Vector3f(centering * nodes[i - 1].x() + eccentricity * boundedGaussian(random, 1.65F),
                    y[i], centering * nodes[i - 1].z() + eccentricity * boundedGaussian(random, 1.65F));
        }
        return nodes;
    }

    private static float boundedGaussian(Random random, float z) {

        return MathHelper.clamp((float) random.nextGaussian(), -z, z);
    }
    // endregion

    // region BEAM

    /**
     * Renders a laser beam in a unit column towards positive y.
     *
     * @param width     Width of the beam.
     * @param coreColor Color/alpha for the center part of the beam.
     * @param glowColor Color/alpha for the glow surrounding the beam.
     */
    public static void renderBeam(PoseStack stack, MultiBufferSource buffer, int packedLight, float width, Color coreColor, Color glowColor) {

        Matrix4f pose = stack.last().pose();
        Vector3f normal = normal(stack);
        Vector4f start = new Vector4f(0, 0, 0, 1).mul(pose);
        Vector4f end = new Vector4f(0, 1, 0, 1).mul(pose);
        Vector2f perp = axialPerp(start, end, width);

        float sx = start.x();
        float sy = start.y();
        float sz = start.z();
        float ex = end.x();
        float ey = end.y();
        float ez = end.z();

        VFXNode[] outer = {new VFXNode(sx + perp.x, sx - perp.x, sy + perp.y, sy - perp.y, sz, width),
                new VFXNode(ex + perp.x, ex - perp.x, ey + perp.y, ey - perp.y, ez, width)};
        perp.mul(0.5F);
        width *= 0.5F;
        VFXNode[] inner = {new VFXNode(sx + perp.x, sx - perp.x, sy + perp.y, sy - perp.y, sz, width),
                new VFXNode(ex + perp.x, ex - perp.x, ey + perp.y, ey - perp.y, ez, width)};

        renderNodesCapped(normal, buffer, LINEAR_GLOW, ROUND_GLOW, packedLight, outer, glowColor);
        renderNodesCapped(normal, buffer, LINEAR_GLOW, ROUND_GLOW, packedLight, inner, coreColor);
    }
    // endregion

    // region WIND
    private static final int WIND_SEGMENTS = 48;
    public static final float WIND_INCR = MathHelper.F_TAU / WIND_SEGMENTS;
    public static final Color WIND_BASE = Color.WHITE.scaleAlpha(0.4F);

    public static Float2FloatFunction getWidthFunc(float width) {

        return index -> width * MathHelper.easePlateau(index);
    }

    public static Color windColor(RandomGenerator random) {

        return WIND_BASE.scaleRGB(random.nextFloat(0.65F, 1.0F));
    }

    public static Color windColor(RandomSource random) {

        return WIND_BASE.scaleRGB(random.nextFloat() * 0.35F + 0.65F);
    }

    /**
     * Renders an axially billboarded streamline that follows the given node positions.
     *
     * @param posns     Desired positions of the nodes.
     * @param widthFunc Function that determines stream width at each node based on the order of nodes.
     *                  Input is a float representing the normalized index of the node (0F for the first node, 1F for the last).
     */
    public static void renderStreamLine(PoseStack stack, VertexConsumer builder, int packedLight, Vector4f[] posns, Color color, Float2FloatFunction widthFunc) {

        if (posns.length < 2) {
            return;
        }
        if (color.a <= 0) {
            return;
        }

        Matrix4f pose = stack.last().pose();
        Vector3f normal = normal(stack);
        for (Vector4f pos : posns) {
            pos.mul(pose);
        }
        int last = posns.length - 1;
        VFXNode[] nodes = new VFXNode[posns.length];
        float increment = 1.0F / last;
        for (int i = 1; i < last; ++i) {
            float width = widthFunc.apply(increment * i);
            nodes[i] = new VFXNode(posns[i], axialPerp(posns[i - 1], posns[i + 1], width), width);
        }
        float width = widthFunc.apply(0.0F);
        nodes[0] = new VFXNode(posns[0], axialPerp(posns[0], posns[1], width), width);
        width = widthFunc.apply(1.0F);
        nodes[last] = new VFXNode(posns[last], axialPerp(posns[last - 1], posns[last], width), width);
        renderNodes(normal, builder, packedLight, nodes, color);
    }

    //public static void renderStreamLine(PoseStack stack, MultiBufferSource buffer, int packedLight, Vector4f[] posns, Color color, Float2FloatFunction widthFunc) {
    //
    //    renderStreamLine(stack, buffer.getBuffer(FLAT_TRANSLUCENT), packedLight, posns, color, widthFunc);
    //}

    public static void renderCyclone(PoseStack stack, VertexConsumer consumer, int packedLight, Color color, float radius, float thickness, int length, float rot, float y) {

        Vector4f[] nodes = new Vector4f[length];
        for (int j = 0; j < length; ++j) {
            float angle = j * WIND_INCR + rot;
            nodes[j] = new Vector4f(MathHelper.cos(angle) * radius, y, MathHelper.sin(angle) * radius, 1.0F);
        }
        renderStreamLine(stack, consumer, packedLight, nodes, color, getWidthFunc(thickness));
    }

    public static void renderCyclone(PoseStack stack, VertexConsumer consumer, int packedLight, Color color, float radius, float thickness, float height, RandomGenerator rand, float time) {

        //int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * (MathHelper.bevel((float) rand.nextDouble(4.0F) + time * 0.06F) + 1.0F), 0, 255);
        time += rand.nextFloat(420);
        renderCyclone(stack, consumer, packedLight, color, radius, thickness, rand.nextInt(WIND_SEGMENTS / 2, WIND_SEGMENTS), (rand.nextFloat(-1F, 1F) + 6F) * time, (rand.nextFloat() + MathHelper.cos(time * 0.2F)) * 0.25F * height);
    }
    // endregion

    public static class VFXNode {

        public final float xp, xn;
        public final float yp, yn;
        public final float z;
        public final float width;

        public VFXNode(float xp, float xn, float yp, float yn, float z, float width) {

            this.xp = xp;
            this.xn = xn;
            this.yp = yp;
            this.yn = yn;
            this.z = z;
            this.width = width;
        }

        public VFXNode(Vector4f pos, Vector2f perp, float width) {

            this(pos.x() + perp.x, pos.x() - perp.x, pos.y() + perp.y, pos.y() - perp.y, pos.z(), width);
        }

        public VFXNode(float xp, float xn, float yp, float yn, float z) {

            this(xp, xn, yp, yn, z, MathHelper.dist(xp - xn, yp - yn));
        }

        public float xMid() {

            return (xp + xn) * 0.5F;
        }

        public float yMid() {

            return (yp + yn) * 0.5F;
        }

        public VFXNode renderStart(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float v0, float u1, float v1) {

            builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
            builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
            return this;
        }

        public VFXNode renderStart(Vector3f normal, VertexConsumer builder, int packedLight, Color color) {

            return renderStart(normal, builder, packedLight, color, 0, 0, 1, 1);
        }

        public VFXNode renderEnd(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float v0, float u1, float v1) {

            builder.vertex(xn, yn, z).color(col.r, col.g, col.b, col.a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
            builder.vertex(xp, yp, z).color(col.r, col.g, col.b, col.a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal.x, normal.y, normal.z).endVertex();
            return this;
        }

        public VFXNode renderEnd(Vector3f normal, VertexConsumer builder, int packedLight, Color color) {

            return renderEnd(normal, builder, packedLight, color, 0, 0, 1, 1);
        }

        public VFXNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {

            renderEnd(normal, builder, packedLight, col, u0, v0, u1, v1);
            renderStart(normal, builder, packedLight, col, u2, v2, u3, v3);
            return this;
        }

        public VFXNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float v0, float u1, float v1, float u2, float v2) {

            return renderMid(normal, builder, packedLight, col, u0, v0, u1, v1, u1, v1, u2, v2);
        }

        public VFXNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col, float u0, float v0, float u1, float v1) {

            return renderMid(normal, builder, packedLight, col, u0, v0, u1, v1, u0, v0, u1, v1);
        }

        public VFXNode renderMid(Vector3f normal, VertexConsumer builder, int packedLight, Color col) {

            return renderMid(normal, builder, packedLight, col, 0, 0, 1, 1);
        }

        @Override
        public String toString() {

            return "{" + xp + ", " + xn + "}, {" + yp + ", " + yn + "}, " + z;
        }

    }

}
