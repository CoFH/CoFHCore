package cofh.lib.fluid;

import cofh.lib.item.IContainerItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import static cofh.lib.item.ContainerType.FLUID;
import static cofh.lib.util.constants.NBTTags.TAG_AMOUNT;
import static cofh.lib.util.constants.NBTTags.TAG_FLUID;

/**
 * Implement this interface on Item classes that support external manipulation of their internal fluid storage.
 * <p>
 * NOTE: Use of NBT data on the containing ItemStack is encouraged.
 *
 * @author King Lemming
 */
public interface IFluidContainerItem extends IContainerItem {

    default CompoundNBT getOrCreateTankTag(ItemStack container) {

        return container.getOrCreateTag();
    }

    default int getSpace(ItemStack container) {

        return getCapacity(container) - getFluidAmount(container);
    }

    default int getScaledFluidStored(ItemStack container, int scale) {

        return MathHelper.round((double) getFluidAmount(container) * scale / getCapacity(container));
    }

    default int getFluidAmount(ItemStack container) {

        return getFluid(container).getAmount();
    }

    /**
     * @param container ItemStack which is the fluid container.
     * @return FluidStack representing the fluid in the container, EMPTY if the container is empty.
     */
    default FluidStack getFluid(ItemStack container) {

        CompoundNBT tag = getOrCreateTankTag(container);
        if (!tag.contains(TAG_FLUID)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tag.getCompound(TAG_FLUID));
    }

    /**
     * @param container ItemStack which is the fluid container.
     * @param resource  FluidStack being queried.
     * @return TRUE if the fluid is valid in this particular container.
     */
    default boolean isFluidValid(ItemStack container, FluidStack resource) {

        return true;
    }

    /**
     * @param container ItemStack which is the fluid container.
     * @return Capacity of this fluid container.
     */
    int getCapacity(ItemStack container);

    /**
     * @param container ItemStack which is the fluid container.
     * @param resource  FluidStack attempting to fill the container.
     * @param action    If SIMULATE, the fill will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) filled into the container.
     */
    default int fill(ItemStack container, FluidStack resource, FluidAction action) {

        CompoundNBT containerTag = getOrCreateTankTag(container);
        if (resource.isEmpty() || !isFluidValid(container, resource)) {
            return 0;
        }
        int capacity = getCapacity(container);

        if (isCreative(container, FLUID)) {
            if (action.execute()) {
                CompoundNBT fluidTag = resource.writeToNBT(new CompoundNBT());
                fluidTag.putInt(TAG_AMOUNT, capacity);
                containerTag.put(TAG_FLUID, fluidTag);
            }
            return resource.getAmount();
        }
        if (action.simulate()) {
            if (!containerTag.contains(TAG_FLUID)) {
                return Math.min(capacity, resource.getAmount());
            }
            FluidStack stack = FluidStack.loadFluidStackFromNBT(containerTag.getCompound(TAG_FLUID));
            if (stack.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!stack.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - stack.getAmount(), resource.getAmount());
        }
        if (!containerTag.contains(TAG_FLUID)) {
            CompoundNBT fluidTag = resource.writeToNBT(new CompoundNBT());
            if (capacity < resource.getAmount()) {
                fluidTag.putInt(TAG_AMOUNT, capacity);
                containerTag.put(TAG_FLUID, fluidTag);
                return capacity;
            }
            fluidTag.putInt(TAG_AMOUNT, resource.getAmount());
            containerTag.put(TAG_FLUID, fluidTag);
            return resource.getAmount();
        }
        CompoundNBT fluidTag = containerTag.getCompound(TAG_FLUID);
        FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);
        if (stack.isEmpty() || !stack.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - stack.getAmount();
        if (resource.getAmount() < filled) {
            stack.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            stack.setAmount(capacity);
        }
        containerTag.put(TAG_FLUID, stack.writeToNBT(fluidTag));
        return filled;
    }

    /**
     * @param container ItemStack which is the fluid container.
     * @param maxDrain  Maximum amount of fluid to be removed from the container.
     * @param action    If SIMULATE, the drain will only be simulated.
     * @return Fluidstack holding the amount of fluid that was (or would have been, if simulated) drained from the
     * container.
     */
    default FluidStack drain(ItemStack container, int maxDrain, FluidAction action) {

        CompoundNBT containerTag = getOrCreateTankTag(container);
        if (maxDrain <= 0 || !containerTag.contains(TAG_FLUID)) {
            return FluidStack.EMPTY;
        }
        FluidStack stack = FluidStack.loadFluidStackFromNBT(containerTag.getCompound(TAG_FLUID));
        if (stack.isEmpty()) {
            return FluidStack.EMPTY;
        }
        boolean creative = isCreative(container, FLUID);
        int drained = creative ? maxDrain : Math.min(stack.getAmount(), maxDrain);
        if (action.execute() && !creative) {
            if (maxDrain >= stack.getAmount()) {
                containerTag.remove(TAG_FLUID);
                return stack;
            }
            CompoundNBT fluidTag = containerTag.getCompound(TAG_FLUID);
            fluidTag.putInt(TAG_AMOUNT, fluidTag.getInt(TAG_AMOUNT) - drained);
            containerTag.put(TAG_FLUID, fluidTag);
        }
        stack.setAmount(drained);
        return stack;
    }

}
