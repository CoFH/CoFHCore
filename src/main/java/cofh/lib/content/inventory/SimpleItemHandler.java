package cofh.lib.content.inventory;

import cofh.lib.api.IStorageCallback;
import net.minecraft.world.item.ItemStack;
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
    protected IStorageCallback callback;
    protected List<ItemStorageCoFH> slots;

    public SimpleItemHandler(@Nonnull List<ItemStorageCoFH> slots) {

        this(null, slots);
    }

    public SimpleItemHandler(@Nullable IStorageCallback callback) {

        this.callback = callback;
        this.slots = new ArrayList<>();
    }

    public SimpleItemHandler(@Nullable IStorageCallback callback, @Nonnull List<ItemStorageCoFH> slots) {

        this.callback = callback;
        this.slots = new ArrayList<>(slots);
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

        if (callback == null) {
            return;
        }
        callback.onInventoryChanged(slot);
    }

    // region IItemHandler
    @Override
    public int getSlots() {

        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        if (slot < 0 || slot >= getSlots()) {
            return ItemStack.EMPTY;
        }
        return slots.get(slot).getItemStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (slot < 0 || slot >= getSlots()) {
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

        if (slot < 0 || slot >= getSlots()) {
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

        if (slot < 0 || slot >= getSlots()) {
            return 0;
        }
        return slots.get(slot).getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        if (slot < 0 || slot >= getSlots()) {
            return false;
        }
        return slots.get(slot).isItemValid(stack);
    }
    // endregion
}
