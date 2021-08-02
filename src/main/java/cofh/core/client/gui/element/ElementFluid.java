package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;

import static cofh.lib.util.constants.Constants.BUCKET_VOLUME;

public class ElementFluid extends ElementBase {

    public FluidStack fluid;

    public ElementFluid(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementFluid setFluid(FluidStack stack) {

        this.fluid = stack;
        return this;
    }

    public ElementFluid setFluid(Fluid fluid) {

        this.fluid = new FluidStack(fluid, BUCKET_VOLUME);
        return this;
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        RenderHelper.drawFluid(posX(), posY(), fluid, width, height);
    }

    @Override
    public void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

}
