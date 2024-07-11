package cofh.lib.common.inventory;

import cofh.lib.api.item.IInventoryContainerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class InventoryContainerItemWrapper implements IItemHandler {

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

}
