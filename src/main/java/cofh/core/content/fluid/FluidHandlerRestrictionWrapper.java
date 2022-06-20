package cofh.core.content.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidHandlerRestrictionWrapper implements IFluidHandler {

    protected IFluidHandler wrappedHandler;
    protected boolean canFill;
    protected boolean canDrain;

    public FluidHandlerRestrictionWrapper(IFluidHandler wrappedHandler, boolean canFill, boolean canDrain) {

        this.wrappedHandler = wrappedHandler;
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Override
    public int getTanks() {

        return wrappedHandler.getTanks();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return wrappedHandler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {

        return wrappedHandler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return wrappedHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (!canFill) {
            return 0;
        }
        return wrappedHandler.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        if (!canDrain) {
            return FluidStack.EMPTY;
        }
        return wrappedHandler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (!canDrain) {
            return FluidStack.EMPTY;
        }
        return wrappedHandler.drain(maxDrain, action);
    }

}
