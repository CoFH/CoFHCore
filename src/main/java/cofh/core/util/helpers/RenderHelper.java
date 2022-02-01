package cofh.core.util.helpers;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Contains various helper functions to assist with rendering.
 *
 * @author King Lemming
 */
public final class RenderHelper {

    private RenderHelper() {

    }

    public static final float RENDER_OFFSET = 1.0F / 512.0F;
    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
    public static final ResourceLocation MC_FONT_SGA = new ResourceLocation("textures/font/ascii_sga.png");
    public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    // region ACCESSORS
    public static TextureManager engine() {

        return Minecraft.getInstance().getTextureManager();
    }

    public static AtlasTexture textureMap() {

        return Minecraft.getInstance().getModelManager().getAtlas(AtlasTexture.LOCATION_BLOCKS);
    }

    public static Tessellator tessellator() {

        return Tessellator.getInstance();
    }

    public static ItemRenderer renderItem() {

        return Minecraft.getInstance().getItemRenderer();
    }
    // endregion

    // region SHEETS
    public static void setBlockTextureSheet() {

        bindTexture(MC_BLOCK_SHEET);
    }

    public static void setDefaultFontTextureSheet() {

        bindTexture(MC_FONT_DEFAULT);
    }

    public static void setSGAFontTextureSheet() {

        bindTexture(MC_FONT_SGA);
    }
    // endregion

    // region DRAW METHODS
    public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

        if (fluid.isEmpty()) {
            return;
        }
        GL11.glPushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int color = fluid.getFluid().getAttributes().getColor(fluid);
        setBlockTextureSheet();
        setGLColorFromInt(color);
        drawTiledTexture(x, y, getTexture(fluid.getFluid().getAttributes().getStillTexture(fluid)), width, height);
        GL11.glPopMatrix();
    }

    public static int getFluidColor(FluidStack fluid) {

        return fluid.getFluid().getAttributes().getColor(fluid);
    }

    public static void drawIcon(TextureAtlasSprite icon, double z) {

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(0, 16, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(16, 16, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(16, 0, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(0, 0, z).uv(icon.getU0(), icon.getV0());
        tessellator().end();

    }

    public static void drawIcon(double x, double y, double z, TextureAtlasSprite icon, int width, int height) {

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + height, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(x + width, y + height, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(x + width, y, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(x, y, z).uv(icon.getU0(), icon.getV0());
        tessellator().end();
    }

    public static void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

        int drawHeight;
        int drawWidth;

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModalRectFromSprite(x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        resetColor();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawScaledTexturedModalRectFromSprite(int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        float u = minU + (maxU - minU) * width / 16F;
        float v = minV + (maxV - minV) * height / 16F;

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + height, 0).uv(minU, v).endVertex();
        buffer.vertex(x + width, y + height, 0).uv(u, v).endVertex();
        buffer.vertex(x + width, y, 0).uv(u, minV).endVertex();
        buffer.vertex(x, y, 0).uv(minU, minV).endVertex();
        Tessellator.getInstance().end();
    }

    public static void drawStencil(int xStart, int yStart, int xEnd, int yEnd, int flag) {

        RenderSystem.disableTexture();
        GL11.glStencilFunc(GL11.GL_ALWAYS, flag, flag);
        GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);
        GL11.glStencilMask(flag);
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);
        GL11.glClearStencil(0);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.vertex(xStart, yEnd, 0).endVertex();
        buffer.vertex(xEnd, yEnd, 0).endVertex();
        buffer.vertex(xEnd, yStart, 0).endVertex();
        buffer.vertex(xStart, yStart, 0).endVertex();
        Tessellator.getInstance().end();

        RenderSystem.enableTexture();
        GL11.glStencilFunc(GL11.GL_EQUAL, flag, flag);
        GL11.glStencilMask(0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }
    // endregion

    // region MATRIX DRAW METHODS
    public static void drawFluid(MatrixStack matrixStack, int x, int y, FluidStack fluid, int width, int height) {

        if (fluid.isEmpty()) {
            return;
        }
        GL11.glPushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int color = fluid.getFluid().getAttributes().getColor(fluid);
        setBlockTextureSheet();
        setGLColorFromInt(color);
        drawTiledTexture(matrixStack, x, y, getTexture(fluid.getFluid().getAttributes().getStillTexture(fluid)), width, height);
        GL11.glPopMatrix();
    }

    public static void drawIcon(MatrixStack matrixStack, TextureAtlasSprite icon, float z) {

        Matrix4f matrix = matrixStack.last().pose();

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(matrix, 0, 16, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(matrix, 16, 16, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(matrix, 16, 0, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(matrix, 0, 0, z).uv(icon.getU0(), icon.getV0());
        tessellator().end();

    }

    public static void drawIcon(MatrixStack matrixStack, float x, float y, float z, TextureAtlasSprite icon, int width, int height) {

        Matrix4f matrix = matrixStack.last().pose();

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(matrix, x + width, y + height, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(matrix, x + width, y, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(matrix, x, y, z).uv(icon.getU0(), icon.getV0());
        tessellator().end();
    }

    public static void drawTiledTexture(MatrixStack matrixStack, int x, int y, TextureAtlasSprite icon, int width, int height) {

        int drawHeight;
        int drawWidth;

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModalRectFromSprite(matrixStack, x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        resetColor();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawScaledTexturedModalRectFromSprite(MatrixStack matrixStack, int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        float u = minU + (maxU - minU) * width / 16F;
        float v = minV + (maxV - minV) * height / 16F;

        Matrix4f matrix = matrixStack.last().pose();

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, 0).uv(minU, v).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).uv(u, v).endVertex();
        buffer.vertex(matrix, x + width, y, 0).uv(u, minV).endVertex();
        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        Tessellator.getInstance().end();
    }

    public static void drawStencil(MatrixStack matrixStack, int xStart, int yStart, int xEnd, int yEnd, int flag) {

        RenderSystem.disableTexture();
        GL11.glStencilFunc(GL11.GL_ALWAYS, flag, flag);
        GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);
        GL11.glStencilMask(flag);
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);
        GL11.glClearStencil(0);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);

        Matrix4f matrix = matrixStack.last().pose();

        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.vertex(matrix, xStart, yEnd, 0).endVertex();
        buffer.vertex(matrix, xEnd, yEnd, 0).endVertex();
        buffer.vertex(matrix, xEnd, yStart, 0).endVertex();
        buffer.vertex(matrix, xStart, yStart, 0).endVertex();
        Tessellator.getInstance().end();

        RenderSystem.enableTexture();
        GL11.glStencilFunc(GL11.GL_EQUAL, flag, flag);
        GL11.glStencilMask(0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }
    // endregion

    // region PASSTHROUGHS
    public static void disableStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.turnOff();
    }

    public static void enableStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.turnBackOn();
    }

    public static void setupGuiFlatDiffuseLighting() {

        net.minecraft.client.renderer.RenderHelper.setupForFlatItems();
    }

    public static void setupGui3DDiffuseLighting() {

        net.minecraft.client.renderer.RenderHelper.setupFor3DItems();
    }
    // endregion

    // region TEXTURE GETTERS
    public static TextureAtlasSprite getTexture(String location) {

        return textureMap().getSprite(new ResourceLocation(location));
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {

        return textureMap().getSprite(location);
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid) {

        return getTexture(fluid.getAttributes().getStillTexture());
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {

        return getTexture(fluid.getFluid().getAttributes().getStillTexture(fluid));
    }

    public static boolean textureExists(String location) {

        return textureExists(new ResourceLocation(location));
    }

    public static boolean textureExists(ResourceLocation location) {

        return !(getTexture(location) instanceof MissingTextureSprite);
    }
    // endregion

    private static int vertexColorIndex;

    static {
        VertexFormat from = DefaultVertexFormats.BLOCK; //Always BLOCK as of 1.15

        vertexColorIndex = -1;
        List<VertexFormatElement> elements = from.getElements();
        for (int i = 0; i < from.getElements().size(); ++i) {
            VertexFormatElement element = elements.get(i);
            if (element.getUsage() == VertexFormatElement.Usage.COLOR) {
                vertexColorIndex = i;
                break;
            }
        }
    }

    public static BakedQuad mulColor(BakedQuad quad, int color) {

        VertexFormat from = DefaultVertexFormats.BLOCK; //Always BLOCK as of 1.15

        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = ((color) & 0xFF) / 255f; // blue

        // Cache this somewhere static, it will never change, and you will never use a different format.
        // See above.

        //        int colorIdx = -1;
        //        List<VertexFormatElement> elements = from.getElements();
        //        for (int i = 0; i < from.getElements().size(); ++i) {
        //            VertexFormatElement element = elements.get(i);
        //            if (element.getUsage() == VertexFormatElement.Usage.COLOR) {
        //                colorIdx = i;
        //                break;
        //            }
        //        }

        int[] packedData = quad.getVertices().clone();
        float[] data = new float[4];
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(packedData, data, from, v, vertexColorIndex);
            data[0] = MathHelper.clamp(data[0] * r, 0, 1);
            data[1] = MathHelper.clamp(data[1] * g, 0, 1);
            data[2] = MathHelper.clamp(data[2] * b, 0, 1);
            LightUtil.pack(data, packedData, from, v, vertexColorIndex);
        }
        return new BakedQuad(packedData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    public static void setGLColorFromInt(int color) {

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, 1.0F);
    }

    public static void resetColor() {

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void bindTexture(ResourceLocation texture) {

        engine().bind(texture);
    }

    public static void renderItemOnBlockSide(MatrixStack matrixStackIn, ItemStack stack, Direction side, BlockPos pos) {

        if (stack.isEmpty() || side.getAxis() == Direction.Axis.Y) {
            return;
        }
        matrixStackIn.pushPose();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        switch (side) {
            case NORTH:
                matrixStackIn.translate(x + 0.75, y + 0.84375, z + RenderHelper.RENDER_OFFSET * 145);
                break;
            case SOUTH:
                matrixStackIn.translate(x + 0.25, y + 0.84375, z + 1 - RenderHelper.RENDER_OFFSET * 145);
                matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));
                break;
            case WEST:
                matrixStackIn.translate(x + RenderHelper.RENDER_OFFSET * 145, y + 0.84375, z + 0.25);
                matrixStackIn.mulPose(new Quaternion(0, 90, 0, true));
                break;
            case EAST:
                matrixStackIn.translate(x + 1 - RenderHelper.RENDER_OFFSET * 145, y + 0.84375, z + 0.75);
                matrixStackIn.mulPose(new Quaternion(0, 270, 0, true));
                break;
            default:
        }
        matrixStackIn.scale(0.03125F, 0.03125F, -RenderHelper.RENDER_OFFSET);
        matrixStackIn.mulPose(new Quaternion(0, 0, 180, true));

        // renderItem().renderAndDecorateItem(stack, 0, 0);

        matrixStackIn.popPose();

        // What of this do I still need?

        //        GlStateManager.enableAlpha();
        //        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        //        GlStateManager.enableBlend();
        //        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        //        GlStateManager.popMatrix();
        //        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

    private static void renderFastItem(@Nonnull ItemStack itemStack, BlockState state, int slot, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime) {

        matrix.pushPose();

        Consumer<IRenderTypeBuffer> finish = (IRenderTypeBuffer buf) -> {
            if (buf instanceof IRenderTypeBuffer.Impl)
                ((IRenderTypeBuffer.Impl) buf).endBatch();
        };

        try {
            matrix.translate(0, 0, 100f);
            matrix.scale(1, -1, 1);
            matrix.scale(16, 16, 16);

            IBakedModel itemModel = renderItem().getModel(itemStack, null, null);
            boolean render3D = itemModel.isGui3d();
            finish.accept(buffer);

            if (render3D) {
                setupGui3DDiffuseLighting();
            } else {
                setupGuiFlatDiffuseLighting();
            }
            matrix.last().normal().set(1, -1, 1);
            renderItem().render(itemStack, ItemCameraTransforms.TransformType.GUI, false, matrix, buffer, combinedLight, combinedOverlay, itemModel);
            finish.accept(buffer);
        } catch (Exception e) {
            // pokemon!
        }
        matrix.popPose();
    }

    public static void renderRectPrism(IVertexBuilder builder, MatrixStack stack, int packedLightIn, Vector3f start, Vector3f end, Vector3f perp) {

        MatrixStack.Entry stackEntry = stack.last();
        Matrix4f pose = stackEntry.pose();
        Matrix3f normal = stackEntry.normal();
        float sx = start.x();
        float sy = start.y();
        float sz = start.z();
        float ex = end.x();
        float ey = end.y();
        float ez = end.z();
        Vector3f diff = end.copy();
        diff.sub(start);
        diff.normalize();
        float p1x = perp.x();
        float p1y = perp.y();
        float p1z = perp.z();
        perp.transform(diff.rotationDegrees(90.0F));
        float p2x = perp.x();
        float p2y = perp.y();
        float p2z = perp.z();

        builder.vertex(pose, sx + p1x + p2x, sy + p1y + p2y, sz + p1z + p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex + p1x + p2x, ey + p1y + p2y, ez + p1z + p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x + p2x, ey - p1y + p2y, ez - p1z + p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx - p1x + p2x, sy - p1y + p2y, sz - p1z + p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(pose, sx + p1x - p2x, sy + p1y - p2y, sz + p1z - p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex + p1x - p2x, ey + p1y - p2y, ez + p1z - p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x - p2x, ey - p1y - p2y, ez - p1z - p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx - p1x - p2x, sy - p1y - p2y, sz - p1z - p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(pose, sx + p1x + p2x, sy + p1y + p2y, sz + p1z + p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex + p1x + p2x, ey + p1y + p2y, ez + p1z + p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex + p1x - p2x, ey + p1y - p2y, ez + p1z - p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx + p1x - p2x, sy + p1y - p2y, sz + p1z - p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(pose, sx - p1x + p2x, sy - p1y + p2y, sz - p1z + p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x + p2x, ey - p1y + p2y, ez - p1z + p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x - p2x, ey - p1y - p2y, ez - p1z - p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx - p1x - p2x, sy - p1y - p2y, sz - p1z - p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(pose, sx + p1x + p2x, sy + p1y + p2y, sz + p1z + p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx - p1x + p2x, sy - p1y + p2y, sz - p1z + p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx - p1x - p2x, sy - p1y - p2y, sz - p1z - p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, sx + p1x - p2x, sy + p1y - p2y, sz + p1z - p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();

        builder.vertex(pose, ex + p1x + p2x, ey + p1y + p2y, ez + p1z + p2z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x + p2x, ey - p1y + p2y, ez - p1z + p2z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex - p1x - p2x, ey - p1y - p2y, ez - p1z - p2z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, ex + p1x - p2x, ey + p1y - p2y, ez + p1z - p2z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
    }

    public static void renderRectPrism(IVertexBuilder builder, MatrixStack stack, int packedLightIn, Vector3f start, Vector3f end, float radius) {

        Vector3f diff = end.copy();
        diff.sub(start);
        Vector3f perp;
        if (diff.z() < 0.0001 && diff.x() < 0.0001) {
            perp = Vector3f.XP.copy();
        } else {
            perp = Vector3f.YP.copy();
        }
        perp.cross(diff);
        perp.normalize();
        perp.mul(radius);
        renderRectPrism(builder, stack, packedLightIn, start, end, perp);
    }

    public static void renderRectPrism(IVertexBuilder builder, MatrixStack stack, int packedLightIn, Vector3f start, Vector3f end, float radius, boolean coverEnds) {

        if (coverEnds) {
            Vector3f ext = end.copy();
            ext.sub(start);
            ext.normalize();
            ext.mul(radius);
            end.add(ext);
            start.sub(ext);
        }
        renderRectPrism(builder, stack, packedLightIn, start, end, radius);
    }

    public static float lengthSqr(Vector3f vec) {

        return vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z();
    }

    public static float length(Vector3f vec) {

        return (float) Math.sqrt(lengthSqr(vec));
    }

    // region ELECTRICITY
    public static Vector3f[][] arcs = getRandomArcs(new Random(), 16, 50);

    /**
     * Renders electric arcs in a unit column towards positive y.
     *
     * @param arcCount      Number of individual arcs.
     * @param arcWidth      Average width of each arc.
     * @param seed          Seed for randomization. Should be changed based on the time.
     * @param taperOffset   Value between -1.25F and 1.25F that determines the threshold for tapering.
     *                      Generally, negative at the start of an animation, 0 in the middle (no taper), and positive at the end.
     */
    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, int arcCount, float arcWidth, long seed, float taperOffset) {

        Random rand = new Random(seed);
        matrixStackIn.pushPose();

        MatrixStack.Entry stackEntry = matrixStackIn.last();
        Matrix4f pose = stackEntry.pose();
        Matrix3f normal = stackEntry.normal();

        int nodeCount = arcs[0].length;
        int first = MathHelper.clamp((int) (nodeCount * taperOffset), 0, nodeCount);
        int last = MathHelper.clamp((int) (nodeCount * (1.25F + taperOffset)), 0, nodeCount);

        //These are calculated first so they are not affected by differing taper values.
        Vector3f[][] randomArcs = new Vector3f[nodeCount][arcCount];
        float[] rotations = new float[arcCount];
        for (int i = 0; i < arcCount; ++i) {
            randomArcs[i] = arcs[rand.nextInt(arcs.length)];
            rotations[i] = rand.nextFloat();
        }
        if (last - first > 1) {
            Vector4f perp = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
            perp.transform(pose);
            float invLength = MathHelper.invSqrt(perp.x() * perp.x() + perp.y() * perp.y());
            float xPerp = perp.y() * invLength;
            float yPerp = -perp.x() * invLength;

            float increment = 1.0F / nodeCount;
            float proportion = 0;
            for (int i = 0; i < arcCount; ++i) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotations[i] * 360.0F));
                Vector3f[] arc = randomArcs[i];
                for (int j = first; j < last; ++j) {
                    Vector4f node = new Vector4f(arc[j].x(), arc[j].y(), arc[j].z(), 1.0F);
                    node.transform(pose);
                    float width = arcWidth * (rand.nextFloat() * 0.6F + 0.7F) * MathHelper.clamp(4.0F * (0.75F - Math.abs(proportion - 0.5F - taperOffset)), 0.0F, 1.0F);
                    float xWidth = xPerp * width;
                    float yWidth = yPerp * width;
                    float xPos = node.x() + xWidth;
                    float yPos = node.y() + yWidth;
                    float xNeg = node.x() - xWidth;
                    float yNeg = node.y() - yWidth;
                    float z = node.z();

                    if (j != first) {
                        builder.vertex(xNeg, yNeg, z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
                        builder.vertex(xPos, yPos, z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
                    }
                    if (j != last - 1) {
                        builder.vertex(xPos, yPos, z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
                        builder.vertex(xNeg, yNeg, z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0, 1, 0).endVertex();
                        proportion += increment;
                    } else {
                        proportion = 0;
                    }
                }
            }
        }
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, int arcCount, float arcWidth, long seed, float time, float taperOffset) {

        renderArcs(matrixStackIn, builder, packedLightIn, arcCount, arcWidth, seed + 69420 * (int) (time * 0.7F), taperOffset);
    }

    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, float length, int arcCount, float arcWidth, long seed, float time, float taperOffset) {

        matrixStackIn.pushPose();
        matrixStackIn.scale(length, length, length);
        renderArcs(matrixStackIn, builder, packedLightIn, arcCount, arcWidth / Math.abs(length), seed, time, taperOffset);
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, Vector3f to, int arcCount, float arcWidth, long seed, float time, float taperOffset) {

        matrixStackIn.pushPose();
        float length = RenderHelper.length(to);
        if (length > 0.01F) {
            if (to.x() < 0.001 && to.z() < 0.001) {
                renderArcs(matrixStackIn, builder, packedLightIn, -length, arcCount, arcWidth, seed, time, taperOffset);
            } else {
                to.normalize();
                to.cross(Vector3f.YP);
                float angle = (float) -Math.asin(RenderHelper.length(to));
                to.normalize();
                matrixStackIn.mulPose(to.rotation(angle));
                renderArcs(matrixStackIn, builder, packedLightIn, length, arcCount, arcWidth, seed, time, taperOffset);
            }
        }
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, Vector3f from, Vector3f to, int arcCount, float arcWidth, long seed, float time, float taperOffset) {

        matrixStackIn.pushPose();
        matrixStackIn.translate(from.x(), from.y(), from.z());
        to.sub(from);
        renderArcs(matrixStackIn, builder, packedLightIn, to, arcCount, arcWidth, seed, time, taperOffset);
        matrixStackIn.popPose();
    }

    public static void renderArcs(MatrixStack matrixStackIn, IVertexBuilder builder, int packedLightIn, Vector3d from, Vector3d to, int arcCount, float arcWidth, long seed, float time, float taperOffset) {

        renderArcs(matrixStackIn, builder, packedLightIn, new Vector3f(from), new Vector3f(to), arcCount, arcWidth, seed, time, taperOffset);
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

        for (int i = nodes.length - 2; i > 0; --i) {
            float eccentricity = 0.3F * (y[i + 1] - y[i]);
            float centering = Math.min(1, 3.0F * i / nodes.length);
            nodes[i] = new Vector3f(centering * nodes[i + 1].x() + (float) random.nextGaussian() * eccentricity, y[i], centering * nodes[i + 1].z() + (float) random.nextGaussian() * eccentricity);
        }
        return nodes;
    }
    // endregion

}
