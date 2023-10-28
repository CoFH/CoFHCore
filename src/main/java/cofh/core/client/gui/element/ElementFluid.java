package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Supplier;

public class ElementFluid extends ElementBase {

    protected Supplier<FluidStack> fluidSup;

    public ElementFluid(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementFluid setFluid(Supplier<FluidStack> sup) {

        this.fluidSup = sup;
        return this;
    }

    @Override
    public void drawBackground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        RenderHelper.drawFluid(guiLeft() + posX(), guiTop() + posY(), fluidSup.get(), width, height);
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        FluidStack fluid = fluidSup.get();
        if (!fluid.isEmpty()) {
            tooltipList.add(StringHelper.getFluidName(fluid));
            if (FluidHelper.hasPotionTag(fluid)) {
                FluidHelper.addPotionTooltip(fluid, tooltipList);
            }
        }
        super.addTooltip(tooltipList, mouseX, mouseY);
    }

}
