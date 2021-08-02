package cofh.lib.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidStackHolder implements IFluidStackAccess {

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
