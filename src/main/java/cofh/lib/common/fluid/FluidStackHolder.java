package cofh.lib.common.fluid;

import cofh.lib.api.fluid.IFluidStackHolder;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidStackHolder implements IFluidStackHolder {

    protected final FluidStack stack;

    public FluidStackHolder(FluidStack stack) {

        this.stack = stack;
    }

    @Nonnull
    @Override
    public FluidStack getFluidStack() {

        return stack;
    }

    @Override
    public int getAmount() {

        return stack.getAmount();
    }

    @Override
    public boolean isEmpty() {

        return stack.isEmpty();
    }

}
