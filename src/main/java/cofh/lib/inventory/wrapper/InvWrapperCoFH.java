package cofh.lib.inventory.wrapper;

import cofh.lib.inventory.SimpleItemInv;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import static cofh.lib.util.constants.Constants.MAX_CAPACITY;

public class InvWrapperCoFH implements IInventory {

    protected SimpleItemInv inventory;
    protected int stackLimit;

    public InvWrapperCoFH(SimpleItemInv inventory) {

        this(inventory, 64);
    }

    public InvWrapperCoFH(SimpleItemInv inventory, int stackLimit) {

        this.inventory = inventory;
        this.stackLimit = MathHelper.clamp(stackLimit, 1, MAX_CAPACITY);
    }

    public int getSlotLimit(int slot) {

        return inventory.getSlotLimit(slot);
    }

    public void onInventoryChange(int slot) {

        inventory.onInventoryChange(slot);
    }

    // region IInventory
    @Override
    public int getSizeInventory() {

        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {

        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        ItemStack inSlot = inventory.get(index);
        if (inSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (inSlot.getCount() <= count) {
            count = inSlot.getCount();
        }
        ItemStack stack = inSlot.split(count);
        if (inSlot.getCount() <= 0) {
            inventory.set(index, ItemStack.EMPTY);
            inventory.onInventoryChange(index);
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {

        ItemStack stack = inventory.get(index);
        inventory.set(index, ItemStack.EMPTY);
        inventory.onInventoryChange(index);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        /* This condition succeeds when the slot is already empty and is being emptied.
        It happens enough to warrant a check and prevent superfluous logic.*/
        if (inventory.get(index).equals(stack)) {
            return;
        }
        inventory.set(index, stack);
        inventory.onInventoryChange(index);
    }

    @Override
    public int getInventoryStackLimit() {

        return stackLimit;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {

        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {

        return inventory.isItemValid(index, stack);
    }

    @Override
    public void clear() {

        inventory.clear();
    }
    // endregion
}
