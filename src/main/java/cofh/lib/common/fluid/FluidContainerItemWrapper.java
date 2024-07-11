package cofh.lib.common.fluid;

import cofh.lib.api.item.IFluidContainerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

/**
 * This class provides a simple way to wrap an IFluidContainerItem to allow for capability support.
 *
 * @author King Lemming
 */
public class FluidContainerItemWrapper implements IFluidHandlerItem {

    protected final ItemStack container;
    protected final IFluidContainerItem item;

    public FluidContainerItemWrapper(ItemStack containerIn, IFluidContainerItem itemIn) {

        this.container = containerIn;
        this.item = itemIn;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {

        return container;
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return item.getFluid(container);
    }

    @Override
    public int getTankCapacity(int tank) {

        return item.getCapacity(container);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack resource) {

        return item.isFluidValid(container, resource);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {

        return item.fill(container, resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {

        if (!resource.isFluidEqual(getFluidInTank(0))) {
            return FluidStack.EMPTY;
        }
        return item.drain(container, resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        return item.drain(container, maxDrain, action);
    }

}
