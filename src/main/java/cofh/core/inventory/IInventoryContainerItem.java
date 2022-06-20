package cofh.core.inventory;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

import static cofh.lib.util.NBTTags.TAG_ITEM_INV;

// TODO: Re-implement if a Holding solution is found.
public interface IInventoryContainerItem { // extends IContainerItem {

    default CompoundTag getOrCreateInvTag(ItemStack container) {

        return container.getOrCreateTagElement(TAG_ITEM_INV);
    }

    SimpleItemInv getContainerInventory(ItemStack container);

    int getContainerSlots(ItemStack container);

    void onContainerInventoryChanged(ItemStack container);

    default int getSpace(ItemStack container, int slot) {

        return getSlotLimit(container, slot) - getItemAmount(container, slot);
    }

    default int getScaledItemsStored(ItemStack container, int slot, int scale) {

        return MathHelper.round((double) getItemAmount(container, slot) * scale / getSlotLimit(container, slot));
    }

    default int getItemAmount(ItemStack container, int slot) {

        return getStackInSlot(container, slot).getCount();
    }

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
