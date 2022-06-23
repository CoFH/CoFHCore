package cofh.lib.fluid;

import net.minecraft.nbt.CompoundTag;
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

    // region NBT
    public FluidStorageCoFH read(CompoundTag nbt) {

        return this;
    }

    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }
    // endregion

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
        return Math.min(resource.getAmount(), getCapacity());
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        return FluidStack.EMPTY;
    }
    // endregion
}
