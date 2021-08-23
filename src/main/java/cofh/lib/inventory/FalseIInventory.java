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
    public int getContainerSize() {

        return 0;
    }

    @Override
    public boolean isEmpty() {

        return true;
    }

    @Override
    public ItemStack getItem(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {

        return false;
    }

    @Override
    public void clearContent() {

    }

}
