package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class ElementButton extends ElementBase {

    public ElementButton(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementButton(IGuiAccess gui, int posX, int posY, int width, int height) {

        super(gui, posX, posY, width, height);
    }

    @Override
    public void drawBackground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        PoseStack poseStack = pGuiGraphics.pose();
        RenderHelper.setShaderTexture0(texture);
        drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);

        if (enabled()) {
            if (intersectsWith(mouseX, mouseY)) {
                drawTexturedModalRect(poseStack, posX(), posY(), width, 0, width, height);
            } else {
                drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
            }
        } else {
            drawTexturedModalRect(poseStack, posX(), posY(), width * 2, 0, width, height);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        return enabled();
    }

}
