package cofh.lib.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EmptyItemStorage extends ItemStorageCoFH {

    public static final EmptyItemStorage INSTANCE = new EmptyItemStorage();

    public ItemStorageCoFH setCapacity(int capacity) {

        return this;
    }

    public void consume(int amount) {

    }

    public void setItemStack(ItemStack item) {

    }

    // region NBT
    @Override
    public ItemStorageCoFH read(CompoundTag nbt) {

        return this;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }
    // endregion

    // region IItemHandler
    @Override
    public int getSlots() {

        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {

        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        return false;
    }
    // endregion
}
