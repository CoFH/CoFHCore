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
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

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

        return vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z();
    }

    public static float length(Vector3f vec) {

        return (float) Math.sqrt(lengthSqr(vec));
    }

    //public static float axialPerp(MatrixStack stack, Vector3f axis, Vector3f start, Vector3f end) {
    //
    //    Matrix3f normal = stack.last().normal();
    //    Matrix4f pose = stack.last().pose();
    //    Vector4f start = new Vector4f(0, 0, 0, 1);
    //    start.transform(pose);
    //    Vector4f end = new Vector4f(0, 1, 1, 1);
    //    end.transform(pose);
    //    float ratio = end.z() / start.z();
    //    float x = -(end.y() - start.y() * ratio);
    //    float y = (end.x() - start.x() * ratio);
    //    float normalize = MathHelper.invDist(x, y);
    //    x *= 1.0F * normalize;
    //    y *= 1.0F * normalize;
    //}

    // region SHOCKWAVE
    private static final SortedMap<Float, List<int[]>> shockwaveOffsets = getOffsets(16);
    public static final List<RenderType> chunkRenderTypes = RenderType.chunkBufferLayers();

    /**
     * Renders a shockwave that propagates from the origin.
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

        VFXNode node = nodes[0];
        float nw = width / node.width;
        float xw = (node.xp - node.xn) * nw;
        float yw = (node.yp - node.yn) * nw;
        builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        int count = nodes.length - 1;
        for (int i = 1; i < count; ++i) {
            node = nodes[i];
            nw = width / node.width;
            xw = (node.xp - node.xn) * nw;
            yw = (node.yp - node.yn) * nw;
            builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }
        node = nodes[count];
        nw = width / node.width;
        xw = (node.xp - node.xn) * nw;
        yw = (node.yp - node.yn) * nw;
        builder.vertex(node.xp, node.yp, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xp + xw, node.yp + yw, node.z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        node = nodes[0];
        nw = width / node.width;
        xw = (node.xp - node.xn) * nw;
        yw = (node.yp - node.yn) * nw;
        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        for (int i = 1; i < count; ++i) {
            node = nodes[i];
            nw = width / node.width;
            xw = (node.xp - node.xn) * nw;
            yw = (node.yp - node.yn) * nw;
            builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }
        node = nodes[count];
        nw = width / node.width;
        xw = (node.xp - node.xn) * nw;
        yw = (node.yp - node.yn) * nw;
        builder.vertex(node.xn - xw, node.yn - yw, node.z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.xn, node.yn, node.z).color(r, g, b, a).uv(0.5F, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    private static void renderArcCore(Matrix3f normal, IRenderTypeBuffer buffer, int packedLight, VFXNode[] nodes, float width, int a, int r, int g, int b) {

        IVertexBuilder builder = buffer.getBuffer(RenderTypes.BLANK_TRANSLUCENT);

        nodes[0].renderStart(normal, builder, packedLight, a, r, g, b);
        int count = nodes.length - 1;
        for (int i = 1; i < count; ++i) {
            nodes[i].renderMid(normal, builder, packedLight, a, r, g, b);
        }
        nodes[count].renderEnd(normal, builder, packedLight, a, r, g, b);
    }

    /**
     * Renders electric arcs in a unit column towards positive y.
     *
     * @param arcCount      Number of individual arcs.
     * @param arcWidth      Average width of each arc. 0.4F recommended.
     * @param seed          Seed for randomization. Should be changed based on the time.
     * @param taperOffset   Value between -1.25F and 1.25F that determines the threshold for tapering.
     *                      Generally, negative at the start of an animation, 0 in the middle (no taper), and positive at the end.
     */
    public static void renderArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        SplittableRandom rand = new SplittableRandom(seed);
        matrixStackIn.pushPose();

        int nodeCount = arcs[0].length;
        int first = MathHelper.clamp((int) (nodeCount * (taperOffset - 0.25F)), 0, nodeCount);
        int last = MathHelper.clamp((int) (nodeCount * (1.25F + taperOffset)) + 1, 0, nodeCount);

        if (last - first > 1) {
            MatrixStack.Entry stackEntry = matrixStackIn.last();
            Matrix4f pose = stackEntry.pose();
            Matrix3f normal = stackEntry.normal();

            //These are calculated first so they are not affected by differing taper values.
            Vector3f[][] randomArcs = new Vector3f[nodeCount][arcCount];
            float[] rotations = new float[arcCount];
            for (int i = 0; i < arcCount; ++i) {
                randomArcs[i] = arcs[rand.nextInt(arcs.length)];
                rotations[i] = (float) rand.nextDouble();
            }

            Vector4f perp = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
            perp.transform(pose);
            float invLength = MathHelper.invDist(perp.x(), perp.y());
            float xPerp = -perp.y() * invLength;
            float yPerp = perp.x() * invLength;

            float incr = 1.0F / nodeCount;
            for (int i = 0; i < arcCount; ++i) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotations[i] * 360.0F));
                Vector3f[] arc = randomArcs[i];
                VFXNode[] nodes = new VFXNode[last - first];
                for (int j = first; j < last; ++j) {
                    Vector4f pos = new Vector4f(arc[j].x(), arc[j].y(), arc[j].z(), 1.0F);
                    pos.transform(pose);
                    float width = arcWidth * ((float) rand.nextDouble() * 0.6F + 0.7F) * MathHelper.clamp(4.0F * (0.75F - Math.abs(j * incr - 0.5F - taperOffset)), 0.0F, 1.0F);
                    float xw = xPerp * width;
                    float yw = yPerp * width;
                    nodes[j - first] = new VFXNode(pos.x() + xw, pos.x() - xw, pos.y() + yw, pos.y() - yw, pos.z(), width);
                }
                renderArcCore(normal, buffer, packedLightIn, nodes, arcWidth, 0xFF, 0xFF, 0xFF, 0xFF);
                renderArcGlow(normal, buffer, packedLightIn, nodes, arcWidth, (argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
            }
        }
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, float length, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        matrixStackIn.pushPose();
        matrixStackIn.scale(length, length, length);
        renderArcs(matrixStackIn, buffer, packedLightIn, arcCount, arcWidth / Math.abs(length), seed, argb, taperOffset);
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, Vector3f to, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        matrixStackIn.pushPose();
        float length = length(to);
        if (length > 0.01F) {
            if (Math.abs(to.x() + to.z()) < 0.001) {
                renderArcs(matrixStackIn, buffer, packedLightIn, to.y(), arcCount, arcWidth, seed, argb, taperOffset);
            } else {
                to.normalize();
                to.cross(Vector3f.YP);
                float angle = (float) -Math.asin(length(to));
                to.normalize();
                matrixStackIn.mulPose(to.rotation(angle));
                renderArcs(matrixStackIn, buffer, packedLightIn, length, arcCount, arcWidth, seed, argb, taperOffset);
            }
        }
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, Vector3f from, Vector3f to, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        matrixStackIn.pushPose();
        matrixStackIn.translate(from.x(), from.y(), from.z());
        to.sub(from);
        renderArcs(matrixStackIn, buffer, packedLightIn, to, arcCount, arcWidth, seed, argb, taperOffset);
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, Vector3d from, Vector3d to, int arcCount, float arcWidth, long seed, int argb, float taperOffset) {

        renderArcs(matrixStackIn, buffer, packedLightIn, new Vector3f(from), new Vector3f(to), arcCount, arcWidth, seed, argb, taperOffset);
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
    public static Function<Float, Float> windTaper = progress -> MathHelper.clamp(3.0F * (0.5F - Math.abs(progress - 0.5F)), 0.0F, 1.0F);

    public static int packARGB(float a, float r, float g, float b) {

        return packARGB((int) (255.0F * a), (int) (255.0F * r), (int) (255.0F * g), (int) (255.0F * b));
    }

    public static int packARGB(int a, int r, int g, int b) {

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static class WindRenderType extends RenderType {

        public WindRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {

            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final RenderType WIND = RenderType.create("wind",
                DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256,
                RenderType.State.builder().setTextureState(NO_TEXTURE)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(true));

    }

    public static void renderStreamLine(MatrixStack stack, IVertexBuilder builder, int packedLight, float width, Vector4f[] nodes, Vector2f perp, int r, int g, int b, int a, Function<Float, Float> taperFunc) {

        if (nodes.length <= 0) {
            return;
        }
        if (a <= 0) {
            return;
        }

        MatrixStack.Entry stackEntry = stack.last();
        Matrix4f pose = stackEntry.pose();
        Matrix3f normal = stackEntry.normal();

        Vector4f node = nodes[0];
        node.transform(pose);
        builder.vertex(node.x(), node.y(), node.z()).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.x(), node.y(), node.z()).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

        int count = nodes.length - 1;
        float increment = 1.0F / nodes.length;
        for (int i = 1; i < count; ++i) {
            node = nodes[i];
            node.transform(pose);
            float nodeWidth = width * taperFunc.apply(increment);
            float xWidth = perp.x * nodeWidth;
            float yWidth = perp.y * nodeWidth;
            float xPos = node.x() + xWidth;
            float yPos = node.y() + yWidth;
            float xNeg = node.x() - xWidth;
            float yNeg = node.y() - yWidth;
            float z = node.z();

            builder.vertex(xNeg, yNeg, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xPos, yPos, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

            builder.vertex(xPos, yPos, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xNeg, yNeg, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }
        node = nodes[count];
        node.transform(pose);
        builder.vertex(node.x(), node.y(), node.z()).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(node.x(), node.y(), node.z()).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    public static void renderStreamLine(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float width, Vector4f[] nodes, Vector2f perp, int r, int g, int b, int a, Function<Float, Float> taperFunc) {

        renderStreamLine(stack, buffer.getBuffer(WindRenderType.WIND), packedLight, width, nodes, perp, r, g, b, a, taperFunc);
    }

    /**
     * Renders a unit wind cyclone that rotates about the Y axis.
     *
     * @param streamCount   The average number of visible streamlines. The actual number will be twice this, to account for fading in and out.
     * @param streamWidth   The average width of streamlines.
     * @param time          Streamlines rotate on average once every 10 time units. Negate to rotate in the opposite direction. Offset for different "seeds."
     * @param alphaScale    Value between 0.0F and 1.0F for the alpha scale. 0.5F recommended.
     */
    public static void renderCyclone(MatrixStack stack, IVertexBuilder builder, int packedLight, int streamCount, float streamWidth, float time, float alphaScale) {

        streamCount *= 2;
        Random rand = new Random(69420);
        MatrixStack.Entry stackEntry = stack.last();
        Matrix4f pose = stackEntry.pose();

        Vector4f perp4f = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
        perp4f.transform(pose);
        float invLength = MathHelper.invSqrt(perp4f.x() * perp4f.x() + perp4f.y() * perp4f.y());
        float xPerp = perp4f.x() * invLength;
        float yPerp = perp4f.y() * invLength;
        Vector2f perp = new Vector2f(xPerp, yPerp);

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotation(time * 0.62832F));
        for (int i = 0; i < streamCount; ++i) {
            float relRot = (rand.nextFloat() - 0.5F) * time * 0.5F + 2 * i;
            float scale = 1.0F + (float) (rand.nextGaussian() + MathHelper.sin(time * 0.1F + i)) * 0.1F;
            float width = streamWidth * (rand.nextFloat() * 0.8F + 0.6F) / scale;
            int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * (MathHelper.bevel(rand.nextFloat() * 4 + time * 0.06F) + 1.0F), 0, 255);
            int brightness = rand.nextInt(32) + 224;
            float height = (float) (rand.nextGaussian() + MathHelper.cos(time * 0.2F + i)) * 0.16F;
            int length = rand.nextInt(16) + 8;
            if (alpha > 0) {
                Vector4f[] nodes = new Vector4f[length];
                stack.pushPose();
                stack.mulPose(Vector3f.YP.rotation(relRot));
                stack.scale(scale, scale, scale);
                for (int j = 0; j < length; ++j) {
                    float angle = j * 0.19635F;
                    nodes[j] = new Vector4f((float) MathHelper.cos(angle) * 0.5F, height, (float) MathHelper.sin(angle) * 0.5F, 1.0F);
                }
                renderStreamLine(stack, builder, packedLight, width, nodes, perp, alpha, brightness, brightness, brightness, windTaper);
                stack.popPose();
            }
        }
        stack.popPose();
    }

    public static void renderCyclone(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float radius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float diameter = radius * 2;
        stack.scale(diameter, height, diameter);
        renderCyclone(stack, buffer.getBuffer(WindRenderType.WIND), packedLight, streamCount, streamWidth, time, alphaScale);
        stack.popPose();
    }

    public static void renderCyclone(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, Vector3f axis, float radius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float length = length(axis);
        if (length > 0.01F) {
            if (Math.abs(axis.x() + axis.z()) > 0.001) {
                axis.normalize();
                axis.cross(Vector3f.YP);
                float angle = (float) -Math.asin(length(axis));
                axis.normalize();
                stack.mulPose(axis.rotation(angle));
            }
            renderCyclone(stack, buffer, packedLight, radius, height, streamCount, streamWidth, time, alphaScale);
        }
        stack.popPose();
    }

    /**
     * Renders a unit wind vortex that rotates about the Y axis.
     *
     * @param streamCount   The average number of visible streamlines. The actual number will be twice this, to account for fading in and out.
     * @param streamWidth   The average width of streamlines.
     * @param time          Streamlines rotate on average once every 10 time units. Negate to rotate in the opposite direction. Offset for different "seeds."
     * @param alphaScale    Value between 0.0F and 1.0F for the alpha scale. 0.5F recommended.
     */
    public static void renderVortex(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, int streamCount, float streamWidth, float outerRadius, float time, float alphaScale) {

        streamCount *= 2;
        Random rand = new Random(69420);
        MatrixStack.Entry stackEntry = stack.last();
        Matrix4f pose = stackEntry.pose();
        Matrix3f normal = stackEntry.normal();
        IVertexBuilder builder = buffer.getBuffer(WindRenderType.WIND); //TODO

        Vector4f perp = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
        perp.transform(pose);
        float invLength = MathHelper.invSqrt(perp.x() * perp.x() + perp.y() * perp.y());
        float xPerp = perp.x() * invLength;
        float yPerp = perp.y() * invLength;

        //float rotTime = 10.0F * outerRadius;
        //float radTime = 0.159155F * rotTime;
        float rotSpeed = 0.314159F / outerRadius;
        stack.pushPose();
        //stack.mulPose(Vector3f.YP.rotation(time * 0.62832F));
        float offset = outerRadius - 0.5F;
        Quaternion rot = Vector3f.YP.rotation(2);
        for (int i = 0; i < streamCount; ++i) {
            float rotOffset = rand.nextFloat() * 6.2832F + time * rotSpeed + 2 * i;
            float scale = 1.0F + (float) rand.nextGaussian() * 0.05F;
            float width = streamWidth * (rand.nextFloat() * 0.8F + 0.6F) / scale;
            int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * MathHelper.clamp(16.0F * Math.abs(MathHelper.frac((rotOffset - 2.4F) * 0.159F) - 0.5F) - 6.0F, 0.0F, 1.0F), 0, 255);
            int brightness = rand.nextInt(32) + 224;
            float height = (float) (rand.nextGaussian() + MathHelper.cos(time * 0.1F + i)) * 0.16F;
            int length = rand.nextInt(4) + 3;
            if (alpha > 0) {
                stack.pushPose();
                stack.scale(scale, scale, scale);
                stack.mulPose(Vector3f.YP.rotation(2 * i));

                stack.pushPose();
                stack.translate(offset, 0, 0);
                stack.mulPose(Vector3f.YP.rotation(rotOffset));
                float increment = 1.0F / length;
                pose = stack.last().pose();
                for (int j = 0; j <= length; ++j) {
                    float angle = j * 0.19635F; //j * 2 * PI / 32
                    Vector4f node = new Vector4f((float) MathHelper.cos(angle) * outerRadius, height, (float) MathHelper.sin(angle) * outerRadius, 1.0F);
                    node.transform(pose);

                    float perpWidth = width * MathHelper.clamp(3.0F * (0.5F - Math.abs(j * increment - 0.5F)), 0.0F, 1.0F);
                    float xWidth = xPerp * perpWidth;
                    float yWidth = yPerp * perpWidth;
                    float xPos = node.x() + xWidth;
                    float yPos = node.y() + yWidth;
                    float xNeg = node.x() - xWidth;
                    float yNeg = node.y() - yWidth;
                    float z = node.z();

                    if (j != 0) {
                        builder.vertex(xNeg, yNeg, z).color(brightness, brightness, brightness, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
                        builder.vertex(xPos, yPos, z).color(brightness, brightness, brightness, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
                    }
                    if (j != length) {
                        builder.vertex(xPos, yPos, z).color(brightness, brightness, brightness, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
                        builder.vertex(xNeg, yNeg, z).color(brightness, brightness, brightness, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
                    }
                }
                stack.popPose();
                stack.popPose();
            }
            stack.mulPose(rot);
        }
        stack.popPose();
    }

    public static void renderVortex(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float inRadius, float outRadius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float diameter = inRadius * 2;
        stack.scale(diameter, height, diameter);
        renderVortex(stack, buffer, packedLight, streamCount, streamWidth, outRadius / inRadius, time, alphaScale);
        stack.popPose();
    }

    public static void renderVortex(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, Vector3f axis, float inRadius, float outRadius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float length = length(axis);
        if (length > 0.01F) {
            if (Math.abs(axis.x() + axis.z()) > 0.001) {
                axis.normalize();
                axis.cross(Vector3f.YP);
                float angle = (float) -Math.asin(length(axis));
                axis.normalize();
                stack.mulPose(axis.rotation(angle));
            }
            renderVortex(stack, buffer, packedLight, inRadius, outRadius, height, streamCount, streamWidth, time, alphaScale);
        }
        stack.popPose();
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

        public VFXNode(float xp, float xn, float yp, float yn, float z) {

            this(xp, xn, yp, yn, z, MathHelper.dist(xp - xn, yp - yn));
        }

        public void renderStart(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            builder.vertex(xp, yp, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xn, yn, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }

        public void renderEnd(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            builder.vertex(xn, yn, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xp, yp, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }

        public void renderMid(Matrix3f normal, IVertexBuilder builder, int packedLight, int a, int r, int g, int b) {

            renderEnd(normal, builder, packedLight, a, r, g, b);
            renderStart(normal, builder, packedLight, a, r, g, b);
        }

    }

}
