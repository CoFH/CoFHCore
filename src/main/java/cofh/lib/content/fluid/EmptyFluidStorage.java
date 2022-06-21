package cofh.lib.content.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EmptyFluidStorage extends FluidStorageCoFH {

    public static final EmptyFluidStorage INSTANCE = new EmptyFluidStorage();

    public EmptyFluidStorage() {

        super(0);
    }

    public FluidStorageCoFH setCapacity(int capacity) {

        return this;
    }

    @Override
    public void setFluidStack(FluidStack stack) {

    }

    // region NBT
    @Override
    public FluidStorageCoFH read(CompoundTag nbt) {

        return this;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }
    // endregion

    // region IFluidHandler
    @Override
    public int getTanks() {

        return 0;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return FluidStack.EMPTY;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {

        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return false;
    }
    // endregion
}
