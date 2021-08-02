package cofh.lib.inventory;

import cofh.lib.util.IInventoryCallback;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Item Handler implementation using CoFH Item Storage objects.
 */
public class SimpleItemHandler implements IItemHandler {

    @Nullable
    protected IInventoryCallback tile;
    protected List<ItemStorageCoFH> slots;

    public SimpleItemHandler() {

        this(null);
    }

    public SimpleItemHandler(@Nullable IInventoryCallback tile) {

        this.tile = tile;
        this.slots = new ArrayList<>();
    }

    public SimpleItemHandler(@Nullable IInventoryCallback tile, @Nonnull List<ItemStorageCoFH> slots) {

        this.tile = tile;
        this.slots = new ArrayList<>(slots);
    }

    public boolean hasSlots() {

        return slots.size() > 0;
    }

    public boolean isEmpty() {

        for (ItemStorageCoFH slot : slots) {
            if (!slot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {

        for (ItemStorageCoFH slot : slots) {
            if (!slot.isFull()) {
                return false;
            }
        }
        return true;
    }

    public void onInventoryChange(int slot) {

        if (tile == null) {
            return;
        }
        tile.onInventoryChange(slot);
    }

    // region IItemHandler
    @Override
    public int getSlots() {

        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        if (slot < 0 || slot > getSlots()) {
            return ItemStack.EMPTY;
        }
        return slots.get(slot).getItemStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (slot < 0 || slot > getSlots()) {
            return stack;
        }
        ItemStack ret = slots.get(slot).insertItem(slot, stack, simulate);

        if (!simulate) {
            onInventoryChange(slot);
        }
        return ret;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (slot < 0 || slot > getSlots()) {
            return ItemStack.EMPTY;
        }
        ItemStack ret = slots.get(slot).extractItem(slot, amount, simulate);

        if (!simulate) {
            onInventoryChange(slot);
        }
        return ret;
    }

    @Override
    public int getSlotLimit(int slot) {

        if (slot < 0 || slot > getSlots()) {
            return 0;
        }
        return slots.get(slot).getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        if (slot < 0 || slot > getSlots()) {
            return false;
        }
        return slots.get(slot).isItemValid(stack);
    }
    // endregion
}
