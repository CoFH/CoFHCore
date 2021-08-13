package cofh.lib.inventory;

import cofh.lib.item.IContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public interface IInventoryContainerItem extends IContainerItem {

    default CompoundNBT getOrCreateInvTag(ItemStack container) {

        return container.getOrCreateTag();
    }

    SimpleItemInv getContainerInventory(ItemStack container);

    int getContainerSlots(ItemStack container);

    void onContainerInventoryChanged(ItemStack container);

    @Nonnull
    default ItemStack getStackInSlot(ItemStack container, int slot) {

        return getContainerInventory(container).getStackInSlot(slot);
    }

    @Nonnull
    default ItemStack insertItem(ItemStack container, int slot, @Nonnull ItemStack stack, boolean simulate) {

        return getContainerInventory(container).insertItem(slot, stack, simulate);
    }

    @Nonnull
    default ItemStack extractItem(ItemStack container, int slot, int amount, boolean simulate) {

        return getContainerInventory(container).extractItem(slot, amount, simulate);
    }

    default int getSlotLimit(ItemStack container, int slot) {

        return getContainerInventory(container).getSlotLimit(slot);
    }

    default boolean isItemValid(ItemStack container, int slot, @Nonnull ItemStack stack) {

        return getContainerInventory(container).isItemValid(slot, stack);
    }

}
