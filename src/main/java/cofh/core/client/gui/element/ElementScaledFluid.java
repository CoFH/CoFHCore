package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;

public class ElementScaledFluid extends ElementScaled {

    protected Supplier<FluidStack> fluidSup;

    public ElementScaledFluid(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementScaledFluid setFluid(Supplier<FluidStack> sup) {

        this.fluidSup = sup;
        return this;
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        RenderHelper.bindTexture(texture);
        int quantity = quantitySup.getAsInt();
        FluidStack fluid = fluidSup.get();

        if (drawBackground) {
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
        switch (direction) {
            case TOP:
                // vertical top -> bottom
                RenderHelper.drawFluid(posX(), posY(), fluid, width, quantity);
                RenderHelper.bindTexture(texture);
                drawTexturedModalRect(posX(), posY(), width, 0, width, quantity);
                return;
            case BOTTOM:
                // vertical bottom -> top
                RenderHelper.drawFluid(posX(), posY() + height - quantity, fluid, width, quantity);
                RenderHelper.bindTexture(texture);
                drawTexturedModalRect(posX(), posY() + height - quantity, width, height - quantity, width, quantity);
                return;
            case LEFT:
                // horizontal left -> right
                RenderHelper.drawFluid(posX(), posY(), fluid, quantity, height);
                RenderHelper.bindTexture(texture);
                drawTexturedModalRect(posX(), posY(), width, 0, quantity, height);
                return;
            case RIGHT:
                // horizontal right -> left
                RenderHelper.drawFluid(posX() + width - quantity, posY(), fluid, quantity, height);
                RenderHelper.bindTexture(texture);
                drawTexturedModalRect(posX() + width - quantity, posY(), width + width - quantity, 0, quantity, height);
        }
    }

}
