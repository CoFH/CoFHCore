package cofh.core.util.helpers.vfx;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public static Vector4f subtract(Vector4f a, Vector4f b) {

        return new Vector4f(a.x() - b.x(), a.y() - b.y(), a.z() - b.z(), a.w() - b.w());
    }

    public static int packARGB(float a, float r, float g, float b) {

        return packARGB((int) (255.0F * a), (int) (255.0F * r), (int) (255.0F * g), (int) (255.0F * b));
    }

    public static int packARGB(int a, int r, int g, int b) {

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static Vector4f mid(Vector4f a, Vector4f b) {

        return new Vector4f((a.x() + b.x()) * 0.5F, (a.y() + b.y()) * 0.5F, (a.z() + b.z()) * 0.5F, (a.w() + b.w()) * 0.5F);
    }

    // region HELPERS
    private static void renderNodes(Matrix3f normal, IVertexBuilder builder, int packedLight, VFXNode[] nodes, int a, int r, int g, int b) {

        nodes[0].renderStart(normal, builder, packedLight, a, r, g, b);
        int count = nodes.length - 1;
        for (int i = 1; i < count; ++i) {
            nodes[i].renderMid(normal, builder, packedLight, a, r, g, b);
        }
        nodes[count].renderEnd(normal, builder, packedLight, a, r, g, b);
    }

    private static void renderSkeleton(Matrix3f normal, IVertexBuilder builder, int packedLight, VFXNode[] nodes) {

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

    public static Vector2f axialPerp(Vector4f start, Vector4f end, float width) {

        width *= 0.5F;
        float ratio = end.z() / start.z();
        float x = end.x() - start.x() * ratio;
        float y = end.y() - start.y() * ratio;
        float normalize = MathHelper.invDist(x, y) * width;
        x *= normalize;
        y *= normalize;
        return new Vector2f(-y, x);
    }

    public static Vector2f axialPerp(Vector3f start, Vector3f end, float width) {

        width *= 0.5F;
        float ratio = end.z() / start.z();
        float x = end.x() - start.x() * ratio;
        float y = end.y() - start.y() * ratio;
        float normalize = MathHelper.invDist(x, y) * width;
        x *= normalize;
        y *= normalize;
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

    //public static VFXNode axialPerp(MatrixStack stack, Vector3f axisStart, Vector3f axisEnd, Vector3f pos, float width) {
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
     * Transforms a matrix stack such that YP faces the given direction.
     * XP, YP, and ZP remain orthogonal to each other.
     */
    public static void transformVertical(MatrixStack stack, Vector3f dir) {

        dir.normalize();
        boolean neg = dir.y() < 0;
        if (dir.x() * dir.x() + dir.z() * dir.z() > 0.001) {
            dir.cross(Vector3f.YP);
            float angle = -MathHelper.asin(length(dir));
            if (neg) {
                angle = -(MathHelper.F_PI + angle);
            }
            dir.normalize();
            stack.mulPose(dir.rotation(angle));
        } else if (neg) {
            stack.mulPose(Vector3f.XP.rotation(MathHelper.F_PI));
        }
    }

    /**
     * Transforms a matrix stack such that YP starts and ends at the given positions.
     * XP, YP, and ZP remain orthogonal to each other. XP and ZP are scaled the same amount as YP.
     */
    public static void transformVertical(MatrixStack stack, Vector3f start, Vector3f end) {

        Vector3f diff = end.copy();
        diff.sub(start);
        float scale = length(diff);
        transformVertical(stack, diff);
        stack.translate(start.x(), start.y(), start.z());
        stack.scale(scale, scale, scale);
    }
    // endregion

    public static void renderTest(MatrixStack stack, IRenderTypeBuffer buffer) {

        IVertexBuilder builder = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        Vector4f center = new Vector4f(0, 0, 0, 1);
        center.transform(stack.last().pose());
        Matrix3f normal = stack.last().normal();
        float xp = center.x() + 0.5F;
        float xn = center.x() - 0.5F;
        float yp = center.y() + 0.5F;
        float yn = center.y() - 0.5F;
        float z = center.z();
        int r = 255;
        int g = 0;
        int b = 255;
        int a = 128;
        int packedLight = 0x00F000F0;
        builder.vertex(xp, yp, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xn, yp, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xn, yn, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xp, yn, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    // region SHOCKWAVE
    private static final SortedMap<Float, List<int[]>> shockwaveOffsets = getOffsets(16);
    public static final List<RenderType> chunkRenderTypes = RenderType.chunkBufferLayers();

    /**
     * Renders a block shockwave that radially propagates from the origin.
     *
     * @param origin        Center of the shockwave.
     * @param time          Travels outward 1 block per unit time. Blocks take 5 units to complete their trajectory.
     *                      Scale this value based on how fast you want the animation to play.
     * @param radius        The maximum radius of the shockwave. Hard limit of 16.
     * @param heightScale   Adjusts how high the blocks travel.
     * @param canRender     Predicate for filtering which blocks are to be rendered.
     */
    public static void renderShockwave(MatrixStack stack, IRenderTypeBuffer buffer, IBlockDisplayReader world, BlockPos origin, float time, float radius, float heightScale, Predicate<BlockPos> canRender) {

        SortedMap<Float, List<int[]>> blocks = shockwaveOffsets.subMap(Math.min(time - 5, radius), Math.min(time, radius + 1));
        for (Float dist : blocks.keySet()) {
            float progress = time - dist;
            double height = heightScale * 0.16 * (radius - dist * 0.5F) * progress * (5 - progress) / radius;
            for (int[] offset : blocks.get(dist)) {
                for (int y = 1; y >= -1; --y) {
                    BlockPos pos = origin.offset(offset[0], y, offset[1]);
                    BlockState state = world.getBlockState(pos);
                    if (canRender.test(pos)) {
                        if (state.getRenderShape() == BlockRenderType.MODEL) {
                            stack.pushPose();
                            stack.translate(offset[0], height + y, offset[1]);
                            stack.scale(1.01F, 1.01F, 1.01F);
                            for (RenderType type : chunkRenderTypes) {
                                if (RenderTypeLookup.canRenderInLayer(state, type)) {
                                    ForgeHooksClient.setRenderLayer(type);
                                    BlockRendererDispatcher renderer = RenderHelper.renderBlock();
                                    renderer.getModelRenderer().renderModel(world, renderer.getBlockModel(state), state, pos.relative(Direction.UP), stack, buffer.getBuffer(type), false, new Random(), state.getSeed(pos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                                }
                            }
                            stack.popPose();
                        }
                        break;
                    }
                }
            }
        }
        ForgeHooksClient.setRenderLayer(null);
    }

    public static void renderShockwave(MatrixStack stack, IRenderTypeBuffer buffer, IBlockDisplayReader world, BlockPos origin, float time, float radius, float heightScale) {

        renderShockwave(stack, buffer, world, origin, time, radius, heightScale, pos -> {
            BlockState state = world.getBlockState(pos);
            return !state.isAir(world, pos) && state.isRedstoneConductor(world, pos) && state.getHarvestLevel() <= 5 &&
                    state.isCollisionShapeFullBlock(world, pos) && !state.hasTileEntity() &&
                    !world.getBlockState(pos.above()).isCollisionShapeFullBlock(world, pos.above());
        });
    }

    private static SortedMap<Float, List<int[]>> getOffsets(int maxRadius) {

        SortedMap<Float, List<int[]>> blocks = new TreeMap<>();
        float maxSqr = maxRadius * maxRadius;
        for (int x = 0; x <= net.minecraft.util.math.MathHelper.ceil(maxRadius); ++x) {
            for (int z = 0; z <= x; ++z) {
                int distSqr = x * x + z * z;
                if (distSqr < maxSqr) {
                    float dist = net.minecraft.util.math.MathHelper.sqrt(distSqr);
                    if (!blocks.containsKey(dist)) {
                        blocks.put(dist, new ArrayList<>());
                    }
                    addReflections(blocks.get(dist), x, z);
                }
            }
        }
        return blocks;
    }

    private static void addReflections(List<int[]> list, int x, int z) {

        list.add(new int[]{x, z});
        list.add(new int[]{-x, -z});
        if (z != 0) {
            list.add(new int[]{-x, z});
            list.add(new int[]{x, -z});
        }
        if (x != 0 && x != z) {
            list.add(new int[]{z, x});
            list.add(new int[]{-z, -x});
            if (z != 0) {
                list.add(new int[]{-z, x});
                list.add(new int[]{z, -x});
            }
        }
    }
    // endregion

    // region ELECTRICITY
    private static final Vector3f[][] arcs = getRandomArcs(new Random(), 16, 50);

    private static void renderArcGlow(Matrix3f normal, IRenderTypeBuffer buffer, int packedLight, VFXNode[] nodes, float width, int a, int r, int g, int b) {

        IVertexBuilder builder = buffer.getBuffer(RenderTypes.LINEAR_GLOW);
        Function<VFXNode, Float> glowWidth = node -> (width * 0.3F + node.width * 0.7F) / node.width;
        Function<VFXNode, VFXNode> posGlow = node -> {
            float w = glowWidth.apply(node);
            return new VFXNode(node.xp + (node.xp - node.xn) * w, node.xp, node.yp + (node.yp - node.yn) * w, node.yp, node.z, w);
        };
        Function<VFXNode, VFXNode> negGlow = node -> {
            float w = glowWidth.apply(node);
            return new VFXNode(node.xn, node.xn - (node.xp - node.xn) * w, node.yn, node.yn - (node.yp - node.yn) * w, node.z, w);
        };
        int count = nodes.length - 1;

        posGlow.apply(nodes[0]).renderStart(normal, builder, packedLight, a, r, g, b, 0, 0, 0.5F, 1);
        for (int i = 1; i < count; ++i) {
            posGlow.apply(nodes[i]).renderMid(normal, builder, packedLight, a, r, g, b, 0, 0, 0.5F, 1);
        }
        posGlow.apply(nodes[count]).renderEnd(normal, builder, packedLight, a, r, g, b, 0, 0, 0.5F, 1);

        negGlow.apply(nodes[0]).renderStart(normal, builder, packedLight, a, r, g, b, 0.5F, 0, 1, 1);
        for (int i = 1; i < count; ++i) {
            negGlow.apply(nodes[i]).renderMid(normal, builder, packedLight, a, r, g, b, 0.5F, 0, 1, 1);
        }
        negGlow.apply(nodes[count]).renderEnd(normal, builder, packedLight, a, r, g, b, 0.5F, 0, 1, 1);

        // Ends
        VFXNode node = nodes[count];
        float nw = glowWidth.apply(node);
        float xw = (node.xp - node.xn) * nw;
        float yw = (node.yp - node.yn) * nw;

        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn + yw, node.yn - xw, node.z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + yw, node.yp - xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        builder = buffer.getBuffer(RenderTypes.ROUND_GLOW);
        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + yw, node.yp - xw, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + xw + yw, node.yp + yw - xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(1, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - xw + yw, node.yn - yw - xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn + yw, node.yn - xw, node.z).color(r, g, b, a).uv(1, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        node = nodes[0];
        nw = glowWidth.apply(node);
        xw = (node.xp - node.xn) * nw;
        yw = (node.yp - node.yn) * nw;

        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + xw - yw, node.yp + yw + xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp - yw, node.yp + xw, node.z).color(r, g, b, a).uv(1, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - yw, node.yn + xw, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - xw - yw, node.yn - yw + xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(1, 0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        builder = buffer.getBuffer(RenderTypes.LINEAR_GLOW);
        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp - yw, node.yp + xw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - yw, node.yn + xw, node.z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    public static void renderArc(Matrix3f normal, IRenderTypeBuffer buffer, int packedLight, VFXNode[] nodes, float width, int argb) {

        renderNodes(normal, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, nodes, 0xFF, 0xFF, 0xFF, 0xFF);
        renderArcGlow(normal, buffer, packedLight, nodes, width, (argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
    }

    /**
     * Renders straight electric arcs in a unit column towards positive y.
     *
     * @param arcCount      Number of individual arcs.
     * @param arcWidth      Average width of each arc. 0.4F recommended.
     * @param seed          Seed for randomization. Should be changed based on the time.
     * @param argb          Color/alpha for the glow surrounding the white core.
     * @param taperOffset   Value between -1.25F and 1.25F that determines the threshold for tapering.
     *                      Generally, negative at the start of an animation, 0 in the middle (no taper), and positive at the end.
     */
    public static void renderStraightArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        SplittableRandom rand = new SplittableRandom(seed);
        matrixStackIn.pushPose();

        int nodeCount = arcs[0].length;
        int first = MathHelper.clamp((int) (nodeCount * (taperOffset - 0.25F) + 1), 0, nodeCount);
        int last = MathHelper.clamp((int) (nodeCount * (1.25F + taperOffset) + 1), 0, nodeCount);

        if (last - first > 1) {
            MatrixStack.Entry stackEntry = matrixStackIn.last();
            Matrix4f pose = stackEntry.pose();
            Matrix3f normal = stackEntry.normal();

            Vector4f start = new Vector4f(0, 0, 0, 1);
            start.transform(pose);
            Vector4f end = new Vector4f(0, 1, 0, 1);
            end.transform(pose);
            Vector2f perp = axialPerp(start, end, 1.0F);

            //These are calculated first so they are not affected by differing taper values.
            Vector3f[][] randomArcs = new Vector3f[nodeCount][arcCount];
            float[] rotations = new float[arcCount];
            for (int i = 0; i < arcCount; ++i) {
                randomArcs[i] = arcs[rand.nextInt(arcs.length)];
                rotations[i] = (float) rand.nextDouble(360.0F);
            }

            float incr = 1.0F / nodeCount;
            for (int i = 0; i < arcCount; ++i) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotations[i]));
                Vector3f[] arc = randomArcs[i];
                VFXNode[] nodes = new VFXNode[last - first];
                for (int j = first; j < last; ++j) {
                    Vector4f center = new Vector4f(0, arc[j].y(), 0, 1.0F);
                    center.transform(pose);
                    Vector4f pos = new Vector4f(arc[j]);
                    pos.transform(pose);
                    float dot = subtract(pos, center).dot(new Vector4f(perp.x, perp.y, 0, 0));
                    float xc = center.x() + perp.x * dot * 2.0F;
                    float yc = center.y() + perp.y * dot * 2.0F;
                    float width = arcWidth * ((float) rand.nextDouble() * 0.6F + 0.7F) * MathHelper.clamp(4.0F * (0.75F - Math.abs(j * incr - 0.5F - taperOffset)), 0.0F, 1.0F);
                    float xw = perp.x * width;
                    float yw = perp.y * width;
                    nodes[j - first] = new VFXNode(xc + xw, xc - xw, yc + yw, yc - yw, center.z(), width);
                }
                renderArc(normal, buffer, packedLightIn, nodes, arcWidth, argb);
            }
        }
        matrixStackIn.popPose();
    }

    public static void renderStraightArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, float length, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        matrixStackIn.pushPose();
        matrixStackIn.scale(length, length, length);
        renderStraightArcs(matrixStackIn, buffer, packedLightIn, arcCount, arcWidth / Math.abs(length), seed, argb, taperOffset);
        matrixStackIn.popPose();
    }

    public static long getSeedWithTime(long seed, float time, float flickerRate) {

        return seed + 69420 * (int) (time * flickerRate);
    }

    public static long getSeedWithTime(long seed, float time) {

        return getSeedWithTime(seed, time, 0.8F);
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

        float[] y = new float[count];
        for (int i = 0; i < y.length; ++i) {
            y[i] = random.nextFloat();
        }
        Arrays.sort(y);
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

    // region WIND
    private static final int WIND_SEGMENTS = 48;
    private static final float WIND_INCR = MathHelper.F_TAU / WIND_SEGMENTS;

    public static Function<Float, Float> getWidthFunc(float width) {

        return index -> width * MathHelper.easePlateau(index);
    }

    /**
     * Renders an axially billboarded streamline that generally (not exactly, for performance reasons) follows the given node positions.
     *
     * @param poss          Desired positions of the nodes. Technically speaking, n positions are connected into n - 1 lines, used to determine the billboard angle.
     *                      The actual nodes that are rendered are midpoints of these lines. If all of that means nothing to you, you're not alone.
     * @param widthFunc     Function that determines stream width at each node based on the order of nodes.
     *                      Input is a float representing the normalized index of the node (0F for the first node, 1F for the last).
     */
    public static void renderStreamLine(MatrixStack stack, IVertexBuilder builder, int packedLight, Vector4f[] poss, int argb, Function<Float, Float> widthFunc) {

        if (poss.length < 3) {
            return;
        }
        int a = (argb >> 24) & 0xFF;
        if (a <= 0) {
            return;
        }
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        MatrixStack.Entry stackEntry = stack.last();
        Matrix4f pose = stackEntry.pose();
        Matrix3f normal = stackEntry.normal();

        for (Vector4f pos : poss) {
            pos.transform(pose);
        }
        int count = poss.length - 1;
        VFXNode[] nodes = new VFXNode[count];
        float increment = 1.0F / (count - 1);
        for (int i = 0; i < count; ++i) {
            Vector4f start = poss[i];
            Vector4f end = poss[i + 1];
            float width = widthFunc.apply(increment * i);
            nodes[i] = new VFXNode(mid(start, end), axialPerp(start, end, width), width);
        }
        renderNodes(normal, builder, packedLight, nodes, a, r, g, b);
    }

    public static void renderStreamLine(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, Vector4f[] poss, int argb, Function<Float, Float> widthFunc) {

        renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, poss, argb, widthFunc);
    }

    /**
     * Renders a unit wind cyclone that rotates about the Y axis.
     *
     * @param streamCount   The average number of visible streamlines. The actual number will be twice this, to account for fading in and out.
     * @param streamWidth   The average width of streamlines.
     * @param time          Streamlines rotate on average once every time unit. Negate to rotate in the opposite direction. Offset for different "seeds."
     * @param alphaScale    Value between 0.0F and 1.0F for the alpha scale. 0.5F recommended.
     */
    public static void renderCyclone(MatrixStack stack, IVertexBuilder builder, int packedLight, int streamCount, float streamWidth, float time, float alphaScale) {

        SplittableRandom rand = new SplittableRandom(69420);
        streamCount *= 2;

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotation(time * 6.2832F));
        for (int i = 0; i < streamCount; ++i) {
            float relRot = ((float) rand.nextDouble() - 0.5F) * time * 0.5F + 2 * i;
            float scale = 1.0F + ((float) rand.nextDouble() + MathHelper.sin(time * 0.1F + i)) * 0.1F;
            float width = streamWidth * ((float) rand.nextDouble() * 0.8F + 0.6F) / scale;
            int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * (MathHelper.bevel((float) rand.nextDouble() * 4 + time * 0.06F) + 1.0F), 0, 255);
            int value = rand.nextInt(32) + 224;
            float y = ((float) rand.nextDouble() + MathHelper.cos(time * 0.2F + i)) * 0.16F;
            int length = rand.nextInt(WIND_SEGMENTS / 2) + WIND_SEGMENTS / 2;
            if (alpha > 0) {
                Vector4f[] nodes = new Vector4f[length];
                stack.pushPose();
                stack.mulPose(Vector3f.YP.rotation(relRot));
                stack.scale(scale, scale, scale);
                for (int j = 0; j < length; ++j) {
                    float angle = j * WIND_INCR;
                    nodes[j] = new Vector4f(MathHelper.cos(angle) * 0.5F, y, MathHelper.sin(angle) * 0.5F, 1.0F);
                }
                renderStreamLine(stack, builder, packedLight, nodes, packARGB(alpha, value, value, value), getWidthFunc(width));
                stack.popPose();
            }
        }
        stack.popPose();
    }

    public static void renderCyclone(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float radius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float diameter = radius * 2;
        stack.scale(diameter, height, diameter);
        renderCyclone(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, streamCount, streamWidth, time, alphaScale);
        stack.popPose();
    }

    /*
     * Renders a unit wind vortex that rotates about the Y axis.
     *
     * @param streamCount   The average number of visible streamlines. The actual number will be twice this, to account for fading in and out.
     * @param streamWidth   The average width of streamlines.
     * @param time          Streamlines rotate on average once every 10 time units. Negate to rotate in the opposite direction. Offset for different "seeds."
     * @param alphaScale    Value between 0.0F and 1.0F for the alpha scale. 0.5F recommended.
     */
    //public static void renderVortex(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, int streamCount, float streamWidth, float outerRadius, float time, float alphaScale) {
    //
    //    streamCount *= 2;
    //    Random rand = new Random(69420);
    //    MatrixStack.Entry stackEntry = stack.last();
    //    Matrix4f pose = stackEntry.pose();
    //    Matrix3f normal = stackEntry.normal();
    //    IVertexBuilder builder = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT); //TODO
    //
    //    Vector4f perp = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
    //    perp.transform(pose);
    //    float invLength = MathHelper.invSqrt(perp.x() * perp.x() + perp.y() * perp.y());
    //    float xPerp = perp.x() * invLength;
    //    float yPerp = perp.y() * invLength;
    //
    //    float rotSpeed = 0.314159F / outerRadius;
    //    stack.pushPose();
    //    //stack.mulPose(Vector3f.YP.rotation(time * 0.62832F));
    //    float offset = outerRadius - 0.5F;
    //    Quaternion rot = Vector3f.YP.rotation(2);
    //    for (int i = 0; i < streamCount; ++i) {
    //        float rotOffset = (float) rand.nextDouble() * 6.2832F + time * rotSpeed + 2 * i;
    //        float scale = 1.0F + (float) rand.nextGaussian() * 0.05F;
    //        float width = streamWidth * ((float) rand.nextDouble() * 0.8F + 0.6F) / scale;
    //        int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * MathHelper.clamp(16.0F * Math.abs(MathHelper.frac((rotOffset - 2.4F) * 0.159F) - 0.5F) - 6.0F, 0.0F, 1.0F), 0, 255);
    //        int brightness = rand.nextInt(32) + 224;
    //        float height = (float) (rand.nextGaussian() + MathHelper.cos(time * 0.1F + i)) * 0.16F;
    //        int length = rand.nextInt(4) + 3;
    //        if (alpha > 0) {
    //            stack.pushPose();
    //            stack.scale(scale, scale, scale);
    //            stack.mulPose(Vector3f.YP.rotation(2 * i));
    //
    //            stack.pushPose();
    //            stack.translate(offset, 0, 0);
    //            stack.mulPose(Vector3f.YP.rotation(rotOffset));
    //            float increment = 1.0F / length;
    //            pose = stack.last().pose();
    //            for (int j = 0; j <= length; ++j) {
    //                float angle = j * 0.19635F; //j * 2 * PI / 32
    //                Vector4f node = new Vector4f((float) MathHelper.cos(angle) * outerRadius, height, (float) MathHelper.sin(angle) * outerRadius, 1.0F);
    //                node.transform(pose);
    //
    //                float perpWidth = width * MathHelper.clamp(3.0F * (0.5F - Math.abs(j * increment - 0.5F)), 0.0F, 1.0F);
    //                float xWidth = xPerp * perpWidth;
    //                float yWidth = yPerp * perpWidth;
    //                float xPos = node.x() + xWidth;
    //                float yPos = node.y() + yWidth;
    //                float xNeg = node.x() - xWidth;
    //                float yNeg = node.y() - yWidth;
    //                float z = node.z();
    //
    //                if (j != 0) {
    //                    builder.vertex(xNeg, yNeg, z).color(brightness, brightness, brightness, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    //                    builder.vertex(xPos, yPos, z).color(brightness, brightness, brightness, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    //                }
    //                if (j != length) {
    //                    builder.vertex(xPos, yPos, z).color(brightness, brightness, brightness, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    //                    builder.vertex(xNeg, yNeg, z).color(brightness, brightness, brightness, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    //                }
    //            }
    //            stack.popPose();
    //            stack.popPose();
    //        }
    //        stack.mulPose(rot);
    //    }
    //    stack.popPose();
    //}
    //
    //public static void renderVortex(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float inRadius, float outRadius, float height, int streamCount, float streamWidth, float time, float alphaScale) {
    //
    //    stack.pushPose();
    //    float diameter = inRadius * 2;
    //    stack.scale(diameter, height, diameter);
    //    renderVortex(stack, buffer, packedLight, streamCount, streamWidth, outRadius / inRadius, time, alphaScale);
    //    stack.popPose();
    //}
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

        public VFXNode renderStart(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b, float uFrom, float vFrom, float uTo, float vTo) {

            builder.vertex(xp, yp, z).color(r, g, b, a).uv(uFrom, vFrom).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xn, yn, z).color(r, g, b, a).uv(uTo, vFrom).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            return this;
        }

        public VFXNode renderEnd(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b, float uFrom, float vFrom, float uTo, float vTo) {

            builder.vertex(xn, yn, z).color(r, g, b, a).uv(uTo, vTo).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xp, yp, z).color(r, g, b, a).uv(uFrom, vTo).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            return this;
        }

        public VFXNode renderMid(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b, float uFrom, float vFrom, float uTo, float vTo) {

            renderEnd(normal, builder, packedLight, a, r, g, b, uFrom, vFrom, uTo, vTo);
            renderStart(normal, builder, packedLight, a, r, g, b, uFrom, vFrom, uTo, vTo);
            return this;
        }

        public VFXNode renderStart(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            return renderStart(normal, builder, packedLight, a, r, g, b, 0, 0, 1, 1);
        }

        public VFXNode renderEnd(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            return renderEnd(normal, builder, packedLight, a, r, g, b, 0, 0, 1, 1);
        }

        public VFXNode renderMid(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            return renderMid(normal, builder, packedLight, a, r, g, b, 0, 0, 1, 1);
        }

    }

}
