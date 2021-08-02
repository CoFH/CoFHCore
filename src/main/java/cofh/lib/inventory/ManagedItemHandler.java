package cofh.lib.inventory;

import cofh.lib.util.IInventoryCallback;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ManagedItemHandler extends SimpleItemHandler {

    protected List<ItemStorageCoFH> inputSlots;
    protected List<ItemStorageCoFH> outputSlots;

    protected boolean preventInputExtract = false;

    public ManagedItemHandler(@Nullable IInventoryCallback tile, @Nonnull List<ItemStorageCoFH> inputSlots, @Nonnull List<ItemStorageCoFH> outputSlots) {

        super(tile);

        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.slots.addAll(inputSlots);
        this.slots.addAll(outputSlots);
    }

    public ManagedItemHandler restrict() {

        preventInputExtract = true;
        return this;
    }

    // region IItemHandler
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (slot < 0 || slot >= inputSlots.size()) {
            return stack;
        }
        ItemStack cur = slots.get(slot).getItemStack();
        ItemStack ret = slots.get(slot).insertItem(slot, stack, simulate);
        if (!simulate && cur.getItem() != slots.get(slot).getItemStack().getItem()) {
            onInventoryChange(slot);
        }
        return ret;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        int minSlot = preventInputExtract ? inputSlots.size() : 0;
        if (slot < minSlot || slot > getSlots()) {
            return ItemStack.EMPTY;
        }
        ItemStack ret = slots.get(slot).extractItem(slot, amount, simulate);

        if (!simulate) {
            onInventoryChange(slot);
        }
        return ret;
    }
    // endregion
}
