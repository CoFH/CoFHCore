package cofh.lib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryContainerItemWrapper implements IItemHandler, ICapabilityProvider {

    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);

    protected final ItemStack container;
    protected final IInventoryContainerItem item;

    public InventoryContainerItemWrapper(ItemStack containerIn, IInventoryContainerItem itemIn) {

        this.container = containerIn;
        this.item = itemIn;
    }

    @Override
    public int getSlots() {

        return item.getContainerSlots(container);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        return item.getStackInSlot(container, slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        return item.insertItem(container, slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        return item.extractItem(container, slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {

        return item.getSlotLimit(container, slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        return item.isItemValid(container, slot, stack);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, holder);
    }

}
