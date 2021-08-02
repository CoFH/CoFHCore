package cofh.lib.inventory.wrapper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class InvWrapperGeneric implements IInventory {

    private NonNullList<ItemStack> stackList;

    private final Container eventHandler;

    public InvWrapperGeneric(Container eventHandler, List<ItemStack> contents, int size) {

        this.stackList = NonNullList.withSize(size, ItemStack.EMPTY);
        this.eventHandler = eventHandler;

        readFromContainerInv(contents);
    }

    public List<ItemStack> getStacks() {

        return stackList;
    }

    public void readFromContainerInv(List<ItemStack> contents) {

        this.stackList.clear();
        for (int i = 0; i < Math.min(contents.size(), getSizeInventory()); ++i) {
            this.stackList.set(i, contents.get(i));
        }
    }

    // region IInventory
    @Override
    public int getSizeInventory() {

        return this.stackList.size();
    }

    public boolean isEmpty() {

        for (ItemStack stack : this.stackList) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {

        return ItemStackHelper.getAndRemove(this.stackList, index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        return ItemStackHelper.getAndSplit(this.stackList, index, count);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        if (index >= 0 && index < getSizeInventory()) {
            this.stackList.set(index, stack);
            this.eventHandler.onCraftMatrixChanged(this);
        }
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {

        return true;
    }

    @Override
    public void clear() {

        this.stackList.clear();
    }
    // endregion
}
