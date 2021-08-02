package cofh.lib.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class provides a simple way to wrap an IFluidContainerItem to allow for capability support.
 *
 * @author King Lemming
 */
public class FluidContainerItemWrapper implements IFluidHandlerItem, ICapabilityProvider {

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

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

    // region ICapabilityProvider
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
    }
    // endregion
}
