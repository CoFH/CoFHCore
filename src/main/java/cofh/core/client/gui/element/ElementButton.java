package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;

public class ElementButton extends ElementBase {

    public ElementButton(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    @Override
    public void drawBackground(PoseStack matrixStack, int mouseX, int mouseY) {

        RenderHelper.setShaderTexture0(texture);
        drawTexturedModalRect(matrixStack, posX(), posY(), 0, 0, width, height);

        if (enabled()) {
            if (intersectsWith(mouseX, mouseY)) {
                drawTexturedModalRect(matrixStack, posX(), posY(), width, 0, width, height);
            } else {
                drawTexturedModalRect(matrixStack, posX(), posY(), 0, 0, width, height);
            }
        } else {
            drawTexturedModalRect(matrixStack, posX(), posY(), width * 2, 0, width, height);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        return enabled();
    }

}
