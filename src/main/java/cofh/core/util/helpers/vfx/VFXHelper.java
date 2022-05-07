package cofh.core.util.helpers.vfx;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
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

    public static int packRGBA(float r, float g, float b, float a) {

        return packRGBA((int) (255.0F * r), (int) (255.0F * g), (int) (255.0F * b), (int) (255.0F * a));
    }

    public static int packRGBA(int r, int g, int b, int a) {

        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    public static Vector4f mid(Vector4f a, Vector4f b) {

        return new Vector4f((a.x() + b.x()) * 0.5F, (a.y() + b.y()) * 0.5F, (a.z() + b.z()) * 0.5F, (a.w() + b.w()) * 0.5F);
    }

    // region HELPERS
    private static void renderNodes(Matrix3f normal, VertexConsumer builder, int packedLight, VFXNode[] nodes, int r, int g, int b, int a) {

        nodes[0].renderStart(normal, builder, packedLight, r, g, b, a);
        int count = nodes.length - 1;
        for (int i = 1; i < count; ++i) {
            nodes[i].renderMid(normal, builder, packedLight, r, g, b, a);
        }
        nodes[count].renderEnd(normal, builder, packedLight, r, g, b, a);
    }

    public static Vec2 axialPerp(Vector4f start, Vector4f end, float width) {

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
        return new Vec2(-y, x);
    }

    public static Vec2 axialPerp(Vector3f start, Vector3f end, float width) {

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
        return new Vec2(-y, x);
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
     * Transforms a matrix stack such that YP faces the given direction.
     * XP, YP, and ZP remain orthogonal to each other.
     */
    public static void transformVertical(PoseStack stack, Vector3f dir) {

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
    public static void transformVertical(PoseStack stack, Vector3f start, Vector3f end) {

        Vector3f diff = end.copy();
        diff.sub(start);
        float scale = length(diff);
        stack.translate(start.x(), start.y(), start.z());
        transformVertical(stack, diff);
        stack.scale(scale, scale, scale);
    }
    // endregion

    // region DEBUG

    /**
     * Renders a billboarded translucent magenta square. Useful for preventing insanity.
     */
    public static void renderTest(PoseStack stack, MultiBufferSource buffer) {

        RenderType type = RenderTypes.FLAT_TRANSLUCENT;
        VertexConsumer builder = buffer.getBuffer(type);
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
    private static final SortedMap<Float, List<int[]>> shockwaveOffsets = getOffsets(16);
    public static final List<RenderType> chunkRenderTypes = RenderType.chunkBufferLayers();

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
    public static void renderShockwave(PoseStack stack, MultiBufferSource buffer, BlockAndTintGetter level, BlockPos origin, float time, float radius, float heightScale, Predicate<BlockPos> canRender) {

        BlockRenderDispatcher renderer = RenderHelper.renderBlock();
        SortedMap<Float, List<int[]>> blocks = shockwaveOffsets.subMap(Math.min(time - 5, radius), Math.min(time, radius + 1));
        for (Float dist : blocks.keySet()) {
            float progress = time - dist;
            double height = heightScale * 0.16 * (radius - dist * 0.5F) * progress * (5 - progress) / radius;
            for (int[] offset : blocks.get(dist)) {
                for (int y = 1; y >= -1; --y) {
                    BlockPos pos = origin.offset(offset[0], y, offset[1]);
                    BlockState state = level.getBlockState(pos);
                    if (canRender.test(pos)) {
                        if (state.getRenderShape() == RenderShape.MODEL) {
                            stack.pushPose();
                            stack.translate(offset[0], height + y, offset[1]);
                            stack.scale(1.01F, 1.01F, 1.01F);
                            for (RenderType type : chunkRenderTypes) {
                                if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
                                    ForgeHooksClient.setRenderType(type);
                                    renderer.renderBatched(state, pos.relative(Direction.UP), level, stack, buffer.getBuffer(type),false, new Random(), EmptyModelData.INSTANCE);
                                    //renderer.getModelRenderer().renderModel(stack.last(), buffer.getBuffer(type), state, renderer.getBlockModel(state), pos.relative(Direction.UP), stack, , false, new Random(), state.getSeed(pos), EmptyModelData.INSTANCE);
                                }
                            }
                            stack.popPose();
                        }
                        break;
                    }
                }
            }
        }
        ForgeHooksClient.setRenderType(null);
    }

    public static void renderShockwave(PoseStack stack, MultiBufferSource buffer, BlockAndTintGetter world, BlockPos origin, float time, float radius, float heightScale) {

        renderShockwave(stack, buffer, world, origin, time, radius, heightScale, pos -> {
            BlockState state = world.getBlockState(pos);
            return !state.isAir() && state.isRedstoneConductor(world, pos) && //TODO: hardness/blast resist?
                    state.isCollisionShapeFullBlock(world, pos) && !state.hasBlockEntity() &&
                    !world.getBlockState(pos.above()).isCollisionShapeFullBlock(world, pos.above());
        });
    }

    private static SortedMap<Float, List<int[]>> getOffsets(int maxRadius) {

        SortedMap<Float, List<int[]>> blocks = new TreeMap<>();
        float maxSqr = maxRadius * maxRadius;
        for (int x = 0; x <= MathHelper.ceil(maxRadius); ++x) {
            for (int z = 0; z <= x; ++z) {
                int distSqr = x * x + z * z;
                if (distSqr < maxSqr) {
                    float dist = MathHelper.sqrt(distSqr);
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

    private static void renderArcGlow(Matrix3f normal, MultiBufferSource buffer, int packedLight, VFXNode[] nodes, float width, int r, int g, int b, int a) {

        VertexConsumer builder = buffer.getBuffer(RenderTypes.LINEAR_GLOW);
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

        // Sides
        posGlow.apply(nodes[0]).renderStart(normal, builder, packedLight, r, g, b, a, 0, 0, 0.5F, 1);
        for (int i = 1; i < count; ++i) {
            posGlow.apply(nodes[i]).renderMid(normal, builder, packedLight, r, g, b, a, 0, 0, 0.5F, 1);
        }
        posGlow.apply(nodes[count]).renderEnd(normal, builder, packedLight, r, g, b, a, 0, 0, 0.5F, 1);

        negGlow.apply(nodes[0]).renderStart(normal, builder, packedLight, r, g, b, a, 0.5F, 0, 1, 1);
        for (int i = 1; i < count; ++i) {
            negGlow.apply(nodes[i]).renderMid(normal, builder, packedLight, r, g, b, a, 0.5F, 0, 1, 1);
        }
        negGlow.apply(nodes[count]).renderEnd(normal, builder, packedLight, r, g, b, a, 0.5F, 0, 1, 1);

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

    public static void renderArc(Matrix3f normal, MultiBufferSource buffer, int packedLight, VFXNode[] nodes, float width, int rgba) {

        renderNodes(normal, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, nodes, 0xFF, 0xFF, 0xFF, 0xFF);
        renderArcGlow(normal, buffer, packedLight, nodes, width, (rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF);
    }

    /**
     * Renders straight electric arcs in a unit column towards positive y.
     *
     * @param arcCount    Number of individual arcs.
     * @param arcWidth    Average width of each arc. 0.4F recommended.
     * @param seed        Seed for randomization. Should be changed based on the time.
     * @param rgba        Color/alpha for the glow surrounding the white core.
     * @param taperOffset Value between -1.25F and 1.25F that determines the threshold for tapering.
     *                    Generally, negative at the start of an animation, 0 in the middle (no taper), and positive at the end.
     */
    public static void renderStraightArcs(PoseStack poseStackIn, MultiBufferSource buffer, int packedLightIn, int arcCount, float arcWidth, long seed, int rgba, float taperOffset) {

        SplittableRandom rand = new SplittableRandom(seed);
        poseStackIn.pushPose();

        int nodeCount = arcs[0].length;
        int first = MathHelper.clamp((int) (nodeCount * (taperOffset - 0.25F) + 1), 0, nodeCount);
        int last = MathHelper.clamp((int) (nodeCount * (1.25F + taperOffset) + 1), 0, nodeCount);

        if (last - first > 1) {
            PoseStack.Pose stackEntry = poseStackIn.last();
            Matrix4f pose = stackEntry.pose();
            Matrix3f normal = stackEntry.normal();

            Vector4f start = new Vector4f(0, 0, 0, 1);
            start.transform(pose);
            Vector4f end = new Vector4f(0, 1, 0, 1);
            end.transform(pose);
            Vec2 perp = axialPerp(start, end, 1.0F);

            //These are calculated first so they are not affected by differing taper values.
            Vector3f[][] randomArcs = new Vector3f[nodeCount][arcCount];
            float[] rotations = new float[arcCount];
            for (int i = 0; i < arcCount; ++i) {
                randomArcs[i] = arcs[rand.nextInt(arcs.length)];
                rotations[i] = (float) rand.nextDouble(360.0F);
            }

            float incr = 1.0F / nodeCount;
            for (int i = 0; i < arcCount; ++i) {
                poseStackIn.mulPose(Vector3f.YP.rotationDegrees(rotations[i]));
                Vector3f[] arc = randomArcs[i];
                VFXNode[] nodes = new VFXNode[last - first];
                for (int j = first; j < last; ++j) {
                    Vector4f center = new Vector4f(0, arc[j].y(), 0, 1.0F);
                    center.transform(pose);
                    Vector4f pos = new Vector4f(arc[j]);
                    pos.transform(pose);
                    float dot = subtract(pos, center).dot(new Vector4f(perp.x, perp.y, 0, 0));
                    float xc = center.x() + perp.x * dot * 3.0F;
                    float yc = center.y() + perp.y * dot * 3.0F;
                    float width = arcWidth * ((float) rand.nextDouble() * 0.6F + 0.7F) * MathHelper.clamp(4.0F * (0.75F - Math.abs(j * incr - 0.5F - taperOffset)), 0.0F, 1.0F);
                    float xw = perp.x * width;
                    float yw = perp.y * width;
                    nodes[j - first] = new VFXNode(xc + xw, xc - xw, yc + yw, yc - yw, center.z(), width);
                }
                renderArc(normal, buffer, packedLightIn, nodes, arcWidth, rgba);
            }
        }
        poseStackIn.popPose();
    }

    public static void renderStraightArcs(PoseStack poseStackIn, MultiBufferSource buffer, int packedLightIn, float length, int arcCount, float arcWidth, long seed, int rgba, float taperOffset) {

        poseStackIn.pushPose();
        poseStackIn.scale(length, length, length);
        renderStraightArcs(poseStackIn, buffer, packedLightIn, arcCount, arcWidth / Math.abs(length), seed, rgba, taperOffset);
        poseStackIn.popPose();
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
     * @param poss      Desired positions of the nodes. Technically speaking, n positions are connected into n - 1 lines, used to determine the billboard angle.
     *                  The actual nodes that are rendered are midpoints of these lines. If all of that means nothing to you, you're not alone.
     * @param widthFunc Function that determines stream width at each node based on the order of nodes.
     *                  Input is a float representing the normalized index of the node (0F for the first node, 1F for the last).
     */
    public static void renderStreamLine(PoseStack stack, VertexConsumer builder, int packedLight, Vector4f[] poss, int rgba, Function<Float, Float> widthFunc) {

        if (poss.length < 3) {
            return;
        }
        int a = rgba & 0xFF;
        if (a <= 0) {
            return;
        }
        int r = (rgba >> 24) & 0xFF;
        int g = (rgba >> 16) & 0xFF;
        int b = (rgba >> 8) & 0xFF;

        PoseStack.Pose stackEntry = stack.last();
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
        renderNodes(normal, builder, packedLight, nodes, r, g, b, a);
    }

    public static void renderStreamLine(PoseStack stack, MultiBufferSource buffer, int packedLight, Vector4f[] poss, int rgba, Function<Float, Float> widthFunc) {

        renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, poss, rgba, widthFunc);
    }

    /**
     * Renders a unit wind cyclone that rotates about the Y axis.
     *
     * @param streamCount The average number of visible streamlines. The actual number will be twice this, to account for fading in and out.
     * @param streamWidth The average width of streamlines.
     * @param time        Streamlines rotate on average once every time unit. Negate to rotate in the opposite direction. Offset for different "seeds."
     * @param alphaScale  Value between 0.0F and 1.0F for the alpha scale. 0.5F recommended.
     */
    public static void renderCyclone(PoseStack stack, VertexConsumer builder, int packedLight, int streamCount, float streamWidth, float time, float alphaScale) {

        SplittableRandom rand = new SplittableRandom(69420);
        streamCount *= 2;

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotation(time * 6.2832F));
        for (int i = 0; i < streamCount; ++i) {
            float relRot = ((float) rand.nextDouble() - 0.5F) * time * 0.5F + 2 * i;
            float scale = 1.0F + ((float) rand.nextDouble() + MathHelper.sin(time * 0.1F + i)) * 0.1F;
            float width = streamWidth * ((float) rand.nextDouble() * 0.8F + 0.6F) / scale;
            int alpha = (int) MathHelper.clamp((64 + rand.nextInt(64)) * alphaScale * (MathHelper.bevel((float) rand.nextDouble(4.0F) + time * 0.06F) + 1.0F), 0, 255);
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
                renderStreamLine(stack, builder, packedLight, nodes, packRGBA(value, value, value, alpha), getWidthFunc(width));
                stack.popPose();
            }
        }
        stack.popPose();
    }

    public static void renderCyclone(PoseStack stack, MultiBufferSource buffer, int packedLight, float radius, float height, int streamCount, float streamWidth, float time, float alphaScale) {

        stack.pushPose();
        float diameter = radius * 2;
        stack.scale(diameter, height, diameter);
        renderCyclone(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, streamCount, streamWidth, time, alphaScale);
        stack.popPose();
    }

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

        public VFXNode(Vector4f pos, Vec2 perp, float width) {

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

        public VFXNode renderStart(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a, float u0, float v0, float u1, float v1) {

            builder.vertex(xp, yp, z).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xn, yn, z).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            return this;
        }

        public VFXNode renderEnd(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a, float u0, float v0, float u1, float v1) {

            builder.vertex(xn, yn, z).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            builder.vertex(xp, yp, z).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
            return this;
        }

        public VFXNode renderMid(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {

            renderEnd(normal, builder, packedLight, r, g, b, a, u0, v0, u1, v1);
            renderStart(normal, builder, packedLight, r, g, b, a, u2, v2, u3, v3);
            return this;
        }

        public VFXNode renderMid(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a, float u0, float v0, float u1, float v1, float u2, float v2) {

            return renderMid(normal, builder, packedLight, r, g, b, a, u0, v0, u1, v1, u1, v1, u2, v2);
        }

        public VFXNode renderMid(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a, float u0, float v0, float u1, float v1) {

            return renderMid(normal, builder, packedLight, r, g, b, a, u0, v0, u1, v1, u0, v0, u1, v1);
        }

        public VFXNode renderStart(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a) {

            return renderStart(normal, builder, packedLight, r, g, b, a, 0, 0, 1, 1);
        }

        public VFXNode renderEnd(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a) {

            return renderEnd(normal, builder, packedLight, r, g, b, a, 0, 0, 1, 1);
        }

        public VFXNode renderMid(Matrix3f normal, VertexConsumer builder, int packedLight, int r, int g, int b, int a) {

            return renderMid(normal, builder, packedLight, r, g, b, a, 0, 0, 1, 1);
        }

    }

}
