package cofh.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class FalseIInventory implements IInventory {

    public static FalseIInventory INSTANCE = new FalseIInventory();

    private FalseIInventory() {

    }

    @Override
    public int getSizeInventory() {

        return 0;
    }

    @Override
    public boolean isEmpty() {

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {

    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {

        return false;
    }

    @Override
    public void clear() {

    }

}
