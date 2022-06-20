package cofh.core.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemHandlerRestrictionWrapper implements IItemHandler {

    protected IItemHandler wrappedHandler;
    protected boolean canInsert;
    protected boolean canExtract;

    public ItemHandlerRestrictionWrapper(IItemHandler wrappedHandler, boolean canInsert, boolean canExtract) {

        this.wrappedHandler = wrappedHandler;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Override
    public int getSlots() {

        return wrappedHandler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        return wrappedHandler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (!canInsert) {
            return stack;
        }
        return wrappedHandler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (!canExtract) {
            return ItemStack.EMPTY;
        }
        return wrappedHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {

        return wrappedHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        return wrappedHandler.isItemValid(slot, stack);
    }

}
