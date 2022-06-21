package cofh.core.client.gui;

import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;

public interface IGuiAccess {

    int getGuiTop();

    int getGuiLeft();

    Font getFontRenderer();

    Player getPlayer();

    int getBlitOffset();

    default void drawIcon(PoseStack matrixStack, TextureAtlasSprite icon, int x, int y) {

        RenderHelper.setPosTexShader();
        RenderHelper.setBlockTextureSheet();
        RenderHelper.resetShaderColor();
        GuiComponent.blit(matrixStack, x, y, getBlitOffset(), 16, 16, icon);
    }

    default void drawIcon(PoseStack matrixStack, TextureAtlasSprite icon, int color, int x, int y) {

        RenderHelper.setPosTexShader();
        RenderHelper.setBlockTextureSheet();
        RenderHelper.setSahderColorFromInt(color);
        GuiComponent.blit(matrixStack, x, y, getBlitOffset(), 16, 16, icon);
        RenderHelper.resetShaderColor();
    }

    default void drawSizedRect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {

        if (x1 < x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 < y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);

        Matrix4f mat = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(mat, x1, y2, getBlitOffset()).endVertex();
        buffer.vertex(mat, x2, y2, getBlitOffset()).endVertex();
        buffer.vertex(mat, x2, y1, getBlitOffset()).endVertex();
        buffer.vertex(mat, x1, y1, getBlitOffset()).endVertex();
        Tesselator.getInstance().end();
        RenderSystem.enableTexture();
    }

    default void drawColoredModalRect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {

        int temp;
        if (x1 < x2) {
            temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 < y2) {
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);

        Matrix4f mat = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(mat, x1, y2, getBlitOffset()).endVertex();
        buffer.vertex(mat, x2, y2, getBlitOffset()).endVertex();
        buffer.vertex(mat, x2, y1, getBlitOffset()).endVertex();
        buffer.vertex(mat, x1, y1, getBlitOffset()).endVertex();
        Tesselator.getInstance().end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    default void drawTexturedModalRect(PoseStack poseStack, int x, int y, int textureX, int textureY, int width, int height) {

        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tesselator tessellator = Tesselator.getInstance();

        Matrix4f mat = poseStack.last().pose();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(mat, x, (y + height), getBlitOffset()).uv(((float) textureX * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.vertex(mat, (x + width), (y + height), getBlitOffset()).uv(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.vertex(mat, (x + width), y, getBlitOffset()).uv(((float) (textureX + width) * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        bufferbuilder.vertex(mat, x, y, getBlitOffset()).uv(((float) textureX * 0.00390625F), ((float) textureY * 0.00390625F)).endVertex();
        tessellator.end();
    }

    default void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float texW, float texH) {

        float texU = 1 / texW;
        float texV = 1 / texH;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        Matrix4f mat = poseStack.last().pose();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(mat, x, y + height, getBlitOffset()).uv((u) * texU, (v + height) * texV).endVertex();
        buffer.vertex(mat, x + width, y + height, getBlitOffset()).uv((u + width) * texU, (v + height) * texV).endVertex();
        buffer.vertex(mat, x + width, y, getBlitOffset()).uv((u + width) * texU, (v) * texV).endVertex();
        buffer.vertex(mat, x, y, getBlitOffset()).uv((u) * texU, (v) * texV).endVertex();
        Tesselator.getInstance().end();
    }

}
