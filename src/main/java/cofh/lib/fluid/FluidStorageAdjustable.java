package cofh.lib.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Implementation of a Fluid Storage object. Does NOT implement {@link IFluidTank}.
 * Additional options (fill/drain limits) are provided.
 *
 * @author King Lemming
 */
public class FluidStorageAdjustable extends FluidStorageCoFH {

    protected IntSupplier maxFill = this::getCapacity;
    protected IntSupplier maxDrain = this::getCapacity;

    public FluidStorageAdjustable(int capacity) {

        this(capacity, e -> true);
    }

    public FluidStorageAdjustable(int capacity, Predicate<FluidStack> validator) {

        super(capacity, validator);
    }

    public FluidStorageAdjustable setTransferLimits(IntSupplier maxFill, IntSupplier maxDrain) {

        this.maxFill = maxFill;
        this.maxDrain = maxDrain;
        return this;
    }

    // region IFluidHandler
    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (resource.getAmount() > this.maxFill.getAsInt()) {
            return super.fill(new FluidStack(resource, this.maxFill.getAsInt()), action);
        }
        return super.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (maxDrain > this.maxDrain.getAsInt()) {
            return super.drain(this.maxDrain.getAsInt(), action);
        }
        return super.drain(maxDrain, action);
    }
    // endregion
}
