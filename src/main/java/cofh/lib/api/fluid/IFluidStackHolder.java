package cofh.core.content.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public interface IFluidStackHolder {

    @Nonnull
    FluidStack getFluidStack();

    int getAmount();

    boolean isEmpty();

}
