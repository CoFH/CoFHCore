package cofh.lib.inventory;

import cofh.lib.item.IContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public interface IInventoryContainerItem extends IContainerItem {

    default CompoundNBT getOrCreateInvTag(ItemStack container) {

        return container.getOrCreateTag();
    }

    IItemHandler getInventory(ItemStack container);

    int getSlots(ItemStack container);

    void onInventoryChanged(ItemStack container);

    @Nonnull
    default ItemStack getStackInSlot(ItemStack container, int slot) {

        return getInventory(container).getStackInSlot(slot);
    }

    @Nonnull
    default ItemStack insertItem(ItemStack container, int slot, @Nonnull ItemStack stack, boolean simulate) {

        return getInventory(container).insertItem(slot, stack, simulate);
    }

    @Nonnull
    default ItemStack extractItem(ItemStack container, int slot, int amount, boolean simulate) {

        return getInventory(container).extractItem(slot, amount, simulate);
    }

    default int getSlotLimit(ItemStack container, int slot) {

        return getInventory(container).getSlotLimit(slot);
    }

    default boolean isItemValid(ItemStack container, int slot, @Nonnull ItemStack stack) {

        return getInventory(container).isItemValid(slot, stack);
    }

}
