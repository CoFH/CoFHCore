package cofh.core.util.helpers;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Contains various helper functions to assist with rendering.
 *
 * @author King Lemming
 */
public final class RenderHelper {

    private RenderHelper() {

    }

    public static final double RENDER_OFFSET = 1.0D / 512.0D;
    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
    public static final ResourceLocation MC_FONT_SGA = new ResourceLocation("textures/font/ascii_sga.png");
    public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    // region ACCESSORS
    public static TextureManager engine() {

        return Minecraft.getInstance().getTextureManager();
    }

    public static AtlasTexture textureMap() {

        return Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 16, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(16, 16, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(16, 0, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(0, 0, z).tex(icon.getMinU(), icon.getMinV());
        tessellator().draw();

    }

    public static void drawIcon(double x, double y, double z, TextureAtlasSprite icon, int width, int height) {

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(x + width, y + height, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(x + width, y, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(x, y, z).tex(icon.getMinU(), icon.getMinV());
        tessellator().draw();
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
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        float u = minU + (maxU - minU) * width / 16F;
        float v = minV + (maxV - minV) * height / 16F;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(minU, v).endVertex();
        buffer.pos(x + width, y + height, 0).tex(u, v).endVertex();
        buffer.pos(x + width, y, 0).tex(u, minV).endVertex();
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        Tessellator.getInstance().draw();
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

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(xStart, yEnd, 0).endVertex();
        buffer.pos(xEnd, yEnd, 0).endVertex();
        buffer.pos(xEnd, yStart, 0).endVertex();
        buffer.pos(xStart, yStart, 0).endVertex();
        Tessellator.getInstance().draw();

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

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix, 0, 16, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(matrix, 16, 16, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(matrix, 16, 0, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(matrix, 0, 0, z).tex(icon.getMinU(), icon.getMinV());
        tessellator().draw();

    }

    public static void drawIcon(MatrixStack matrixStack, float x, float y, float z, TextureAtlasSprite icon, int width, int height) {

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix, x, y + height, z).tex(icon.getMinU(), icon.getMaxV());
        buffer.pos(matrix, x + width, y + height, z).tex(icon.getMaxU(), icon.getMaxV());
        buffer.pos(matrix, x + width, y, z).tex(icon.getMaxU(), icon.getMinV());
        buffer.pos(matrix, x, y, z).tex(icon.getMinU(), icon.getMinV());
        tessellator().draw();
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
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        float u = minU + (maxU - minU) * width / 16F;
        float v = minV + (maxV - minV) * height / 16F;

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix, x, y + height, 0).tex(minU, v).endVertex();
        buffer.pos(matrix, x + width, y + height, 0).tex(u, v).endVertex();
        buffer.pos(matrix, x + width, y, 0).tex(u, minV).endVertex();
        buffer.pos(matrix, x, y, 0).tex(minU, minV).endVertex();
        Tessellator.getInstance().draw();
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

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(matrix, xStart, yEnd, 0).endVertex();
        buffer.pos(matrix, xEnd, yEnd, 0).endVertex();
        buffer.pos(matrix, xEnd, yStart, 0).endVertex();
        buffer.pos(matrix, xStart, yStart, 0).endVertex();
        Tessellator.getInstance().draw();

        RenderSystem.enableTexture();
        GL11.glStencilFunc(GL11.GL_EQUAL, flag, flag);
        GL11.glStencilMask(0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }
    // endregion

    // region PASSTHROUGHS
    public static void disableStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }

    public static void enableStandardItemLighting() {

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

    public static void setupGuiFlatDiffuseLighting() {

        net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
    }

    public static void setupGui3DDiffuseLighting() {

        net.minecraft.client.renderer.RenderHelper.setupGui3DDiffuseLighting();
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

        int[] packedData = quad.getVertexData().clone();
        float[] data = new float[4];
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(packedData, data, from, v, vertexColorIndex);
            data[0] = MathHelper.clamp(data[0] * r, 0, 1);
            data[1] = MathHelper.clamp(data[1] * g, 0, 1);
            data[2] = MathHelper.clamp(data[2] * b, 0, 1);
            LightUtil.pack(data, packedData, from, v, vertexColorIndex);
        }
        return new BakedQuad(packedData, quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.applyDiffuseLighting());
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

        engine().bindTexture(texture);
    }

}
