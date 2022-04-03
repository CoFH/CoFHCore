package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.IntSupplier;

public class ElementScaled extends ElementBase {

    protected IntSupplier quantitySup;
    protected boolean drawBackground = true;
    protected StartDirection direction = StartDirection.BOTTOM;

    public ElementScaled(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementScaled drawBackground(boolean drawBackground) {

        this.drawBackground = drawBackground;
        return this;
    }

    public ElementScaled setDirection(StartDirection direction) {

        this.direction = direction;
        return this;
    }

    public ElementScaled setQuantity(IntSupplier sup) {

        this.quantitySup = sup;
        return this;
    }

    @Override
    public void drawBackground(PoseStack poseStack, int mouseX, int mouseY) {

        RenderHelper.setPosTexShader();
        RenderHelper.setShaderTexture0(texture);
        int quantity = quantitySup.getAsInt();

        if (drawBackground) {
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
        switch (direction) {
            case TOP:
                // vertical top -> bottom
                drawTexturedModalRect(poseStack, posX(), posY(), width, 0, width, quantity);
                return;
            case BOTTOM:
                // vertical bottom -> top
                drawTexturedModalRect(poseStack, posX(), posY() + height - quantity, width, height - quantity, width, quantity);
                return;
            case LEFT:
                // horizontal left -> right
                drawTexturedModalRect(poseStack, posX(), posY(), width, 0, quantity, height);
                return;
            case RIGHT:
                // horizontal right -> left
                drawTexturedModalRect(poseStack, posX() + width - quantity, posY(), width + width - quantity, 0, quantity, height);
        }
    }

    public enum StartDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

}
