package cofh.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;

public interface IGuiAccess {

    int getGuiTop();

    int getGuiLeft();

    Font getFontRenderer();

    Player getPlayer();

    boolean handleElementButtonClick(String buttonName, int mouseButton);

    void drawIcon(PoseStack matrixStack, TextureAtlasSprite icon, int x, int y);

    void drawIcon(PoseStack matrixStack, TextureAtlasSprite icon, int color, int x, int y);

    void drawSizedRect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color);

    void drawColoredModalRect(PoseStack poseStack, int x1, int y1, int x2, int y2, int color);

    void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height);

    void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float texW, float texH);

}
