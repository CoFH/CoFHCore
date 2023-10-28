package cofh.core.client.gui;

import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

public interface IGuiAccess {

    int guiTop();

    int guiLeft();

    Font fontRenderer();

    Player player();

    int blitOffset();

    default void drawIcon(GuiGraphics pGuiGraphics, TextureAtlasSprite icon, int x, int y) {

        RenderHelper.setPosTexShader();
        RenderHelper.setBlockTextureSheet();
        RenderHelper.resetShaderColor();
        pGuiGraphics.blit(x, y, blitOffset(), 16, 16, icon);
    }

    default void drawIcon(GuiGraphics pGuiGraphics, TextureAtlasSprite icon, int color, int x, int y) {

        RenderHelper.setPosTexShader();
        RenderHelper.setBlockTextureSheet();
        RenderHelper.setShaderColorFromInt(color);
        pGuiGraphics.blit(x, y, blitOffset(), 16, 16, icon);
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
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);

        Matrix4f mat = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(mat, x1, y2, blitOffset()).endVertex();
        buffer.vertex(mat, x2, y2, blitOffset()).endVertex();
        buffer.vertex(mat, x2, y1, blitOffset()).endVertex();
        buffer.vertex(mat, x1, y1, blitOffset()).endVertex();
        Tesselator.getInstance().end();
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
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);

        Matrix4f mat = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(mat, x1, y2, blitOffset()).endVertex();
        buffer.vertex(mat, x2, y2, blitOffset()).endVertex();
        buffer.vertex(mat, x2, y1, blitOffset()).endVertex();
        buffer.vertex(mat, x1, y1, blitOffset()).endVertex();
        Tesselator.getInstance().end();
        RenderSystem.disableBlend();
    }

    default void drawTexturedModalRect(GuiGraphics pGuiGraphics, int x, int y, int textureX, int textureY, int width, int height) {

        final float f = 0.00390625F;
        Tesselator tessellator = Tesselator.getInstance();

        Matrix4f mat = pGuiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(mat, x, (y + height), blitOffset()).uv(((float) textureX * f), ((float) (textureY + height) * f)).endVertex();
        bufferbuilder.vertex(mat, (x + width), (y + height), blitOffset()).uv(((float) (textureX + width) * f), ((float) (textureY + height) * f)).endVertex();
        bufferbuilder.vertex(mat, (x + width), y, blitOffset()).uv(((float) (textureX + width) * f), ((float) textureY * f)).endVertex();
        bufferbuilder.vertex(mat, x, y, blitOffset()).uv(((float) textureX * f), ((float) textureY * f)).endVertex();
        tessellator.end();
    }

    default void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float texW, float texH) {

        float texU = 1 / texW;
        float texV = 1 / texH;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        Matrix4f mat = poseStack.last().pose();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(mat, x, y + height, blitOffset()).uv((u) * texU, (v + height) * texV).endVertex();
        buffer.vertex(mat, x + width, y + height, blitOffset()).uv((u + width) * texU, (v + height) * texV).endVertex();
        buffer.vertex(mat, x + width, y, blitOffset()).uv((u + width) * texU, (v) * texV).endVertex();
        buffer.vertex(mat, x, y, blitOffset()).uv((u) * texU, (v) * texV).endVertex();
        Tesselator.getInstance().end();
    }

}
