package cofh.core.inventory.wrapper;

import cofh.core.inventory.SimpleItemInv;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static cofh.lib.util.Constants.MAX_CAPACITY;

public class InvWrapperCoFH implements Container {

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
    public int getContainerSize() {

        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {

        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {

        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {

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
    public ItemStack removeItemNoUpdate(int index) {

        ItemStack stack = inventory.get(index);
        inventory.set(index, ItemStack.EMPTY);
        inventory.onInventoryChange(index);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        /* This condition succeeds when the slot is already empty and is being emptied.
        It happens enough to warrant a check and prevent superfluous logic.*/
        if (inventory.get(index).equals(stack)) {
            return;
        }
        inventory.set(index, stack);
        inventory.onInventoryChange(index);
    }

    @Override
    public int getMaxStackSize() {

        return stackLimit;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {

        return true;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {

        return inventory.isItemValid(index, stack);
    }

    @Override
    public void clearContent() {

        inventory.clear();
    }
    // endregion
}
