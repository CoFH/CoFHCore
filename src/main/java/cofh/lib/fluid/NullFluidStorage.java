package cofh.lib.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class NullFluidStorage extends FluidStorageCoFH {

    public NullFluidStorage(int capacity) {

        super(capacity);
    }

    public NullFluidStorage(int capacity, Predicate<FluidStack> validator) {

        super(capacity, validator);
    }

    @Override
    public void setFluidStack(FluidStack stack) {

        // Do Nothing
    }

    // region IFluidHandler
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return FluidStack.EMPTY;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (resource.isEmpty() || !isFluidValid(resource) || !enabled.getAsBoolean()) {
            return 0;
        }
        return resource.getAmount();
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        return FluidStack.EMPTY;
    }
    // endregion
}
