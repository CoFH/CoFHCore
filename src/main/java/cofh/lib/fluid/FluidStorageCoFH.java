package cofh.lib.fluid;

import cofh.lib.util.IResourceStorage;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.constants.NBTTags.TAG_CAPACITY;

/**
 * Implementation of a Fluid Storage object. Does NOT implement {@link IFluidTank}.
 *
 * @author King Lemming
 */
public class FluidStorageCoFH implements IFluidHandler, IFluidStackAccess, IResourceStorage {

    protected final int baseCapacity;

    protected BooleanSupplier creative = FALSE;
    protected BooleanSupplier enabled = TRUE;
    protected Supplier<FluidStack> emptyFluid = EMPTY_FLUID;
    protected Predicate<FluidStack> validator;

    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    public FluidStorageCoFH(int capacity) {

        this(capacity, e -> true);
    }

    public FluidStorageCoFH(int capacity, Predicate<FluidStack> validator) {

        this.baseCapacity = capacity;
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidStorageCoFH applyModifiers(float storageMod) {

        setCapacity(Math.round(baseCapacity * storageMod));
        return this;
    }

    public FluidStorageCoFH setCapacity(int capacity) {

        this.capacity = MathHelper.clamp(capacity, 0, MAX_CAPACITY);
        if (!isEmpty()) {
            fluid.setAmount(Math.max(0, Math.min(capacity, getAmount())));
        }
        return this;
    }

    public FluidStorageCoFH setEmptyFluid(Supplier<FluidStack> emptyFluidSupplier) {

        if (emptyFluidSupplier != null && emptyFluidSupplier.get() != null) {
            this.emptyFluid = emptyFluidSupplier;
        }
        if (fluid.isEmpty()) {
            setFluidStack(this.emptyFluid.get());
        }
        return this;
    }

    public FluidStorageCoFH setCreative(BooleanSupplier creative) {

        this.creative = creative;
        if (!fluid.isEmpty() && isCreative()) {
            fluid.setAmount(getCapacity());
        }
        return this;
    }

    public FluidStorageCoFH setEnabled(BooleanSupplier enabled) {

        if (enabled != null) {
            this.enabled = enabled;
        }
        return this;
    }

    public FluidStorageCoFH setValidator(Predicate<FluidStack> validator) {

        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(@Nonnull FluidStack stack) {

        return enabled.getAsBoolean() && validator.test(stack);
    }

    public void setFluidStack(FluidStack stack) {

        this.fluid = stack.isEmpty() ? emptyFluid.get() : stack;
        if (!fluid.isEmpty() && isCreative()) {
            stack.setAmount(capacity);
        }
    }

    // region NBT
    public FluidStorageCoFH read(CompoundNBT nbt) {

        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluidStack(fluid);
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        fluid.writeToNBT(nbt);
        nbt.putInt(TAG_CAPACITY, baseCapacity);
        return nbt;
    }
    // endregion

    // region IFluidStackAccess
    @Override
    public FluidStack getFluidStack() {

        return fluid;
    }

    @Override
    public int getAmount() {

        return fluid.getAmount();
    }

    @Override
    public boolean isEmpty() {

        return fluid.isEmpty();
    }
    // endregion

    // region IFluidHandler
    @Override
    public int getTanks() {

        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return fluid;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        if (resource.isEmpty() || !isFluidValid(resource) || !enabled.getAsBoolean()) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            setFluidStack(new FluidStack(resource, Math.min(capacity, resource.getAmount())));
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (maxDrain <= 0 || fluid.isEmpty() || !enabled.getAsBoolean()) {
            return FluidStack.EMPTY;
        }
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && !isCreative()) {
            fluid.shrink(drained);
            if (fluid.isEmpty()) {
                setFluidStack(emptyFluid.get());
            }
        }
        return stack;
    }

    @Override
    public int getTankCapacity(int tank) {

        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return isFluidValid(stack);
    }
    // endregion

    // IResourceStorage
    @Override
    public boolean clear() {

        if (isEmpty()) {
            return false;
        }
        this.fluid = emptyFluid.get();
        return true;
    }

    @Override
    public void modify(int amount) {

        if (isCreative()) {
            amount = Math.max(amount, 0);
        }
        fluid.setAmount(Math.min(fluid.getAmount() + amount, getCapacity()));
        if (this.fluid.isEmpty()) {
            this.fluid = emptyFluid.get();
        }
    }

    @Override
    public boolean isCreative() {

        return creative.getAsBoolean();
    }

    @Override
    public int getCapacity() {

        return getTankCapacity(0);
    }

    @Override
    public int getStored() {

        return fluid.getAmount();
    }

    @Override
    public String getUnit() {

        return "mB";
    }
    // endregion
}
