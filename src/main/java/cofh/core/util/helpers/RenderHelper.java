package cofh.core.util.helpers;

import cofh.core.event.CoreClientEvents;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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

    public static final float RENDER_OFFSET = 1.0F / 512.0F;
    public static final int FULL_BRIGHT = 0x00F000F0;
    public static final ResourceLocation MC_BLOCK_SHEET = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation MC_FONT_DEFAULT = new ResourceLocation("textures/font/ascii.png");
    public static final ResourceLocation MC_FONT_SGA = new ResourceLocation("textures/font/ascii_sga.png");
    public static final ResourceLocation MC_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static PoseStack particleStack = new PoseStack();

    // region ACCESSORS
    public static MultiBufferSource.BufferSource bufferSource() {

        return Minecraft.getInstance().renderBuffers().bufferSource();
    }

    public static TextureManager engine() {

        return Minecraft.getInstance().getTextureManager();
    }

    public static TextureAtlas textureMap() {

        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

    public static Tesselator tesselator() {

        return Tesselator.getInstance();
    }

    public static ItemRenderer renderItem() {

        return Minecraft.getInstance().getItemRenderer();
    }

    public static BlockRenderDispatcher renderBlock() {

        return Minecraft.getInstance().getBlockRenderer();
    }

    public static EntityRenderDispatcher renderEntity() {

        return Minecraft.getInstance().getEntityRenderDispatcher();
    }

    public static ModelPart bakeLayer(ModelLayerLocation location) {

        return Minecraft.getInstance().getEntityModels().bakeLayer(location);
    }

    public static int renderTime() {

        return CoreClientEvents.renderTime;
    }

    public static float partialTick() {

        return Minecraft.getInstance().getPartialTick();
    }

    public static float frameDelta() {

        return Minecraft.getInstance().getDeltaFrameTime();
    }
    // endregion

    // region SHEETS
    public static void setBlockTextureSheet() {

        setShaderTexture0(MC_BLOCK_SHEET);
    }

    public static void setDefaultFontTextureSheet() {

        setShaderTexture0(MC_FONT_DEFAULT);
    }

    public static void setSGAFontTextureSheet() {

        setShaderTexture0(MC_FONT_SGA);
    }
    // endregion

    // region DRAW METHODS
    public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

        if (fluid.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        int color = FluidHelper.color(fluid);
        setPosTexShader();
        setBlockTextureSheet();
        setShaderColorFromInt(color);
        drawTiledTexture(x, y, getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid)), width, height);
    }

    public static void drawIcon(TextureAtlasSprite icon, double z) {

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0, 16, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(16, 16, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(16, 0, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(0, 0, z).uv(icon.getU0(), icon.getV0());
        tesselator().end();

    }

    public static void drawIcon(double x, double y, double z, TextureAtlasSprite icon, int width, int height) {

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y + height, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(x + width, y + height, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(x + width, y, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(x, y, z).uv(icon.getU0(), icon.getV0());
        tesselator().end();
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
        resetShaderColor();
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

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y + height, 0).uv(minU, v).endVertex();
        buffer.vertex(x + width, y + height, 0).uv(u, v).endVertex();
        buffer.vertex(x + width, y, 0).uv(u, minV).endVertex();
        buffer.vertex(x, y, 0).uv(minU, minV).endVertex();
        tesselator().end();
    }


    // TODO This is unused, needs PoseStack argument and Shaders set
/*    public static void drawStencil(int xStart, int yStart, int xEnd, int yEnd, int flag) {

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
    }*/
    // endregion

    // region MATRIX DRAW METHODS
    public static void drawFluid(GuiGraphics pGuiGraphics, int x, int y, FluidStack fluid, int width, int height) {

        if (fluid.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int color = FluidHelper.color(fluid);
        setPosTexShader();
        setBlockTextureSheet();
        setShaderColorFromInt(color);

        drawTiledTexture(pGuiGraphics, x, y, getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid)), width, height);
    }

    public static void drawIcon(GuiGraphics pGuiGraphics, TextureAtlasSprite icon, float z) {

        Matrix4f matrix = pGuiGraphics.pose().last().pose();

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, 0, 16, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(matrix, 16, 16, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(matrix, 16, 0, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(matrix, 0, 0, z).uv(icon.getU0(), icon.getV0());
        tesselator().end();

    }

    public static void drawIcon(GuiGraphics pGuiGraphics, float x, float y, float z, TextureAtlasSprite icon, int width, int height) {

        Matrix4f matrix = pGuiGraphics.pose().last().pose();

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, z).uv(icon.getU0(), icon.getV1());
        buffer.vertex(matrix, x + width, y + height, z).uv(icon.getU1(), icon.getV1());
        buffer.vertex(matrix, x + width, y, z).uv(icon.getU1(), icon.getV0());
        buffer.vertex(matrix, x, y, z).uv(icon.getU0(), icon.getV0());
        tesselator().end();
    }

    public static void drawTiledTexture(GuiGraphics pGuiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {

        int drawHeight;
        int drawWidth;

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModalRectFromSprite(pGuiGraphics, x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        resetShaderColor();
    }

    public static void drawScaledTexturedModalRectFromSprite(GuiGraphics pGuiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        float u = minU + (maxU - minU) * width / 16F;
        float v = minV + (maxV - minV) * height / 16F;

        Matrix4f matrix = pGuiGraphics.pose().last().pose();

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y + height, 0).uv(minU, v).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).uv(u, v).endVertex();
        buffer.vertex(matrix, x + width, y, 0).uv(u, minV).endVertex();
        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        tesselator().end();
    }

    public static void drawStencil(PoseStack matrixStack, int xStart, int yStart, int xEnd, int yEnd, int flag) {

        RenderSystem.setShader(GameRenderer::getPositionShader);
        GL11.glStencilFunc(GL11.GL_ALWAYS, flag, flag);
        GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);
        GL11.glStencilMask(flag);
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);
        GL11.glClearStencil(0);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);

        Matrix4f matrix = matrixStack.last().pose();

        BufferBuilder buffer = tesselator().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(matrix, xStart, yEnd, 0).endVertex();
        buffer.vertex(matrix, xEnd, yEnd, 0).endVertex();
        buffer.vertex(matrix, xEnd, yStart, 0).endVertex();
        buffer.vertex(matrix, xStart, yStart, 0).endVertex();
        tesselator().end();

        GL11.glStencilFunc(GL11.GL_EQUAL, flag, flag);
        GL11.glStencilMask(0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
    }
    // endregion

    // region PASSTHROUGHS
    // TODO Fix these if needed.
/*    public static void disableStandardItemLighting() {

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
    }*/
    // endregion

    // region TEXTURE GETTERS
    public static TextureAtlasSprite getTexture(String location) {

        return textureMap().getSprite(new ResourceLocation(location));
    }

    public static TextureAtlasSprite getTexture(ResourceLocation location) {

        return textureMap().getSprite(location);
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid) {

        return getTexture(IClientFluidTypeExtensions.of(fluid).getStillTexture());
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluid) {

        return getTexture(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));
    }

    public static boolean textureExists(String location) {

        return textureExists(new ResourceLocation(location));
    }

    public static boolean textureExists(ResourceLocation location) {

        return getTexture(location).atlasLocation() != MissingTextureAtlasSprite.getLocation();
    }
    // endregion

    private static int vertexColorIndex;

    static {
        VertexFormat from = DefaultVertexFormat.BLOCK; // Always BLOCK as of 1.15

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

        VertexFormat from = DefaultVertexFormat.BLOCK; // Always BLOCK as of 1.15

        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = ((color) & 0xFF) / 255f; // blue

        int[] packedData = quad.getVertices().clone();
        float[] data = new float[4];
        for (int v = 0; v < 4; v++) {
            unpackLight(packedData, data, from, v, vertexColorIndex);
            data[0] = MathHelper.clamp(data[0] * r, 0, 1);
            data[1] = MathHelper.clamp(data[1] * g, 0, 1);
            data[2] = MathHelper.clamp(data[2] * b, 0, 1);
            packLight(data, packedData, from, v, vertexColorIndex);
        }
        return new BakedQuad(packedData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    // region LIGHT UTIL
    public static void unpackLight(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {

        int length = Math.min(4, to.length);
        VertexFormatElement element = formatFrom.getElements().get(e);
        int vertexStart = v * formatFrom.getVertexSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        VertexFormatElement.Usage usage = element.getUsage();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                if (type == VertexFormatElement.Type.FLOAT) {
                    to[i] = Float.intBitsToFloat(bits);
                } else if (type == VertexFormatElement.Type.UBYTE || type == VertexFormatElement.Type.USHORT) {
                    to[i] = (float) bits / mask;
                } else if (type == VertexFormatElement.Type.UINT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
                } else if (type == VertexFormatElement.Type.BYTE) {
                    to[i] = ((float) (byte) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.SHORT) {
                    to[i] = ((float) (short) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.Type.INT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
                }
            } else {
                to[i] = (i == 3 && usage == VertexFormatElement.Usage.POSITION) ? 1 : 0;
            }
        }
    }

    public static void packLight(float[] from, int[] to, VertexFormat formatTo, int v, int e) {

        VertexFormatElement element = formatTo.getElements().get(e);
        int vertexStart = v * formatTo.getVertexSize() + formatTo.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.Type type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < 4; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = 0;
                float f = i < from.length ? from[i] : 0;
                if (type == VertexFormatElement.Type.FLOAT) {
                    bits = Float.floatToRawIntBits(f);
                } else if (
                        type == VertexFormatElement.Type.UBYTE ||
                                type == VertexFormatElement.Type.USHORT ||
                                type == VertexFormatElement.Type.UINT
                ) {
                    bits = Math.round(f * mask);
                } else {
                    bits = Math.round(f * (mask >> 1));
                }
                to[index] &= ~(mask << (offset * 8));
                to[index] |= (((bits & mask) << (offset * 8)));
                // TODO handle overflow into to[index + 1]
            }
        }
    }
    // endregion

    public static void setShaderColorFromInt(int color) {

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }

    public static float red(int color) {

        return (float) (color >> 16 & 255) / 255.0F;
    }

    public static float green(int color) {

        return (float) (color >> 8 & 255) / 255.0F;
    }

    public static float blue(int color) {

        return (float) (color & 255) / 255.0F;
    }

    public static void setPosTexShader() {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static void resetShaderColor() {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void setShaderTexture0(ResourceLocation texture) {

        RenderSystem.setShaderTexture(0, texture);
    }

    public static void renderItemOnBlockSide(PoseStack poseStackIn, ItemStack stack, Direction side, BlockPos pos) {

        if (stack.isEmpty() || side.getAxis() == Direction.Axis.Y) {
            return;
        }
        poseStackIn.pushPose();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        switch (side) {
            case NORTH -> poseStackIn.translate(x + 0.75, y + 0.84375, z + RenderHelper.RENDER_OFFSET * 145);
            case SOUTH -> {
                poseStackIn.translate(x + 0.25, y + 0.84375, z + 1 - RenderHelper.RENDER_OFFSET * 145);
                poseStackIn.mulPose(MathHelper.quaternion(0, 180, 0));
            }
            case WEST -> {
                poseStackIn.translate(x + RenderHelper.RENDER_OFFSET * 145, y + 0.84375, z + 0.25);
                poseStackIn.mulPose(MathHelper.quaternion(0, 90, 0));
            }
            case EAST -> {
                poseStackIn.translate(x + 1 - RenderHelper.RENDER_OFFSET * 145, y + 0.84375, z + 0.75);
                poseStackIn.mulPose(MathHelper.quaternion(0, 270, 0));
            }
            default -> {
            }
        }
        poseStackIn.scale(0.03125F, 0.03125F, -RenderHelper.RENDER_OFFSET);
        poseStackIn.mulPose(MathHelper.quaternion(0, 0, 180));

        // renderItem().renderAndDecorateItem(stack, 0, 0);

        poseStackIn.popPose();

        // What of this do I still need?

        //        GlStateManager.enableAlpha();
        //        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        //        GlStateManager.enableBlend();
        //        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        //        GlStateManager.popMatrix();
        //        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    }

    // TODO Fix if required, 1.17 render changes are required.
/*    private static void renderFastItem(@Nonnull ItemStack itemStack, BlockState state, int slot, PoseStack matrix, MultiBufferSource.BufferSource buffer, int combinedLight, int combinedOverlay, Direction side, float partialTickTime) {

        matrix.pushPose();

        Consumer<MultiBufferSource.BufferSource> finish = (IRenderTypeBuffer buf) -> {
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
    }*/

    // region GEOMETRY
    public static void renderCuboid(AABB aabb, PoseStack poseStack, VertexConsumer buffer, int light, float r, float g, float b, float a, TextureAtlasSprite icon) {

        var mat4 = poseStack.last().pose();
        var mat3 = poseStack.last().normal();

        float u0 = icon.getU0();
        float u1 = icon.getU1();
        float v0 = icon.getV0();
        float v1 = icon.getV1();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
    }

    public static Vector4f[] getCuboidCorners(Matrix4f pose, float w, float l, float h) {

        Vector4f[] corners = {new Vector4f(-w, -l, -h, 1.0F), new Vector4f(-w, -l, h, 1.0F), new Vector4f(w, -l, -h, 1.0F), new Vector4f(w, -l, h, 1.0F),
                new Vector4f(w, l, -h, 1.0F), new Vector4f(w, l, h, 1.0F), new Vector4f(-w, l, -h, 1.0F), new Vector4f(-w, l, h, 1.0F)};

        for (Vector4f corner : corners) {
            MathHelper.transform(corner, pose);
        }
        return corners;
    }

    public static void renderSides(VertexConsumer consumer, int light, Color color, Vector4f[] corners, Vector3f normal, float u0, float v0, float u1, float v1) {

        renderFace(consumer, light, color, corners[1], corners[0], corners[2], corners[3], u0, v0, u1, v1, normal);
        renderFace(consumer, light, color, corners[3], corners[2], corners[4], corners[5], u0, v0, u1, v1, normal);
        renderFace(consumer, light, color, corners[5], corners[4], corners[6], corners[7], u0, v0, u1, v1, normal);
        renderFace(consumer, light, color, corners[7], corners[6], corners[0], corners[1], u0, v0, u1, v1, normal);
    }

    public static void renderBottom(VertexConsumer consumer, int light, Color color, Vector4f[] corners, Vector3f normal, float u0, float v0, float u1, float v1) {

        renderFace(consumer, light, color, corners[6], corners[4], corners[2], corners[0], u0, v0, u1, v1, normal);
    }

    public static void renderTop(VertexConsumer consumer, int light, Color color, Vector4f[] corners, Vector3f normal, float u0, float v0, float u1, float v1) {

        renderFace(consumer, light, color, corners[1], corners[3], corners[5], corners[7], u0, v0, u1, v1, normal);
    }

    public static void renderCuboid(VertexConsumer consumer, int light, Color color, Vector4f[] corners, Vector3f normal, float u0, float v0, float u1, float v1) {

        renderSides(consumer, light, color, corners, normal, u0, v0, u1, v1);
        renderBottom(consumer, light, color, corners, normal, u0, v0, u1, v1);
        renderTop(consumer, light, color, corners, normal, u0, v0, u1, v1);
    }

    public static void renderFace(VertexConsumer consumer, int light, Color color, Vector4f a, Vector4f b, Vector4f c, Vector4f d, float u0, float v0, float u1, float v1, Vector3f normal) {

        consumer.vertex(a.x(), a.y(), a.z()).color(color.r, color.g, color.b, color.a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(b.x(), b.y(), b.z()).color(color.r, color.g, color.b, color.a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(c.x(), c.y(), c.z()).color(color.r, color.g, color.b, color.a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal.x(), normal.y(), normal.z()).endVertex();
        consumer.vertex(d.x(), d.y(), d.z()).color(color.r, color.g, color.b, color.a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }

    /**
     * Renders a trigonal trapezohedron (a cube stretched along one diagonal). Has 6 congruent rhombi as faces.
     *
     * @param height The total height of the polyhedron.
     * @param radius The maximum distance of a point within the polyhedron from its axis.
     */
    public static void renderTrigonalTrapezohedron(PoseStack stack, VertexConsumer consumer, int packedLight, Color col, float height, float radius) {

        int a = col.a;
        if (a <= 0) {
            return;
        }
        int r = col.r;
        int g = col.g;
        int b = col.b;
        PoseStack.Pose last = stack.last();
        Matrix4f pose = last.pose();
        Matrix3f norm = last.normal();
        //float lenSqr = (diagRatio * diagRatio + 1) * 0.25F;
        //float rad = 0.57735F * diagRatio;
        //float h = 0.5F * (float) Math.sqrt(lenSqr - rad * rad);
        float h = 0.1666667F * height;

        Vector4f u = new Vector4f(0, 3 * h, 0, 1);
        Vector4f l = new Vector4f(0, -3 * h, 0, 1);
        Vector4f[] v = new Vector4f[8];
        for (int i = 0; i < 6; ++i) {
            v[i] = new Vector4f(radius * MathHelper.sin(i * 1.0472F), h, radius * MathHelper.cos(i * 1.0472F), 1);
            h = -h;
        }
        v[6] = v[0];
        v[7] = v[1];
        MathHelper.transform(u, pose);
        MathHelper.transform(l, pose);
        for (int i = 0; i < 6; ++i) {
            MathHelper.transform(v[i], pose);
        }
        for (int i = 0; i < 6; i += 2) {
            Vector4f v0 = v[i];
            Vector4f v1 = v[i + 1];
            Vector4f v2 = v[i + 2];
            consumer.vertex(v0.x(), v0.y(), v0.z()).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v1.x(), v1.y(), v1.z()).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v2.x(), v2.y(), v2.z()).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(u.x(), u.y(), u.z()).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
        }
        for (int i = 1; i < 7; i += 2) {
            Vector4f v0 = v[i + 2];
            Vector4f v1 = v[i + 1];
            Vector4f v2 = v[i];
            consumer.vertex(v0.x(), v0.y(), v0.z()).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v1.x(), v1.y(), v1.z()).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v2.x(), v2.y(), v2.z()).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(l.x(), l.y(), l.z()).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
        }
    }

    /**
     * Renders a bipyramid.
     *
     * @param height    The total height of the polyhedron.
     * @param radius    The maximum distance of a point within the polyhedron from its axis.
     * @param baseEdges The number of edges that the base of each pyramid has.
     */
    public static void renderBipyramid(PoseStack stack, VertexConsumer consumer, int packedLight, Color col, int baseEdges, float height, float radius) {

        int a = col.a;
        if (col.a <= 0) {
            return;
        }
        int r = col.r;
        int g = col.g;
        int b = col.b;
        PoseStack.Pose last = stack.last();
        Matrix4f pose = last.pose();
        Matrix3f norm = last.normal();

        Vector4f u = new Vector4f(0, height * 0.5F, 0, 1);
        Vector4f l = new Vector4f(0, height * -0.5F, 0, 1);
        Vector4f[] v = new Vector4f[baseEdges + 1];
        float angle = MathHelper.F_TAU / baseEdges;
        for (int i = 0; i < baseEdges; ++i) {
            v[i] = new Vector4f(radius * MathHelper.sin(i * angle), 0, radius * MathHelper.cos(i * angle), 1);
        }
        v[baseEdges] = v[0];
        MathHelper.transform(u, pose);
        MathHelper.transform(l, pose);
        for (int i = 0; i < baseEdges; ++i) {
            MathHelper.transform(v[i], pose);
        }
        for (int i = 0; i < baseEdges; ++i) {
            Vector4f v0 = v[i];
            Vector4f v1 = v[i + 1];
            consumer.vertex(v0.x(), v0.y(), v0.z()).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v1.x(), v1.y(), v1.z()).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(u.x(), u.y(), u.z()).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(u.x(), u.y(), u.z()).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();

            consumer.vertex(v1.x(), v1.y(), v1.z()).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(v0.x(), v0.y(), v0.z()).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(l.x(), l.y(), l.z()).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
            consumer.vertex(l.x(), l.y(), l.z()).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(norm, 0, 1, 0).endVertex();
        }
    }
    // endregion

}
