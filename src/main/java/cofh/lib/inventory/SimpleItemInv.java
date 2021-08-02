package cofh.lib.inventory;

import cofh.lib.util.IInventoryCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static cofh.lib.util.constants.NBTTags.TAG_ITEM_INV;
import static cofh.lib.util.constants.NBTTags.TAG_SLOT;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

/**
 * Inventory abstraction using CoFH Item Storage objects.
 */
public class SimpleItemInv extends SimpleItemHandler {

    protected String tag;

    public SimpleItemInv(@Nullable IInventoryCallback tile) {

        this(tile, TAG_ITEM_INV);
    }

    public SimpleItemInv(@Nullable IInventoryCallback tile, @Nonnull String tag) {

        this(tile, Collections.emptyList(), tag);
    }

    public SimpleItemInv(@Nullable IInventoryCallback tile, @Nonnull List<ItemStorageCoFH> slots, @Nonnull String tag) {

        super(tile, slots);
        this.tag = tag;
    }

    public void clear() {

        for (ItemStorageCoFH slot : slots) {
            slot.setItemStack(ItemStack.EMPTY);
        }
    }

    public void set(int slot, ItemStack stack) {

        slots.get(slot).setItemStack(stack);
    }

    public ItemStack get(int slot) {

        return slots.get(slot).getItemStack();
    }

    // TODO: Works but not used atm. Commented out for safety.
    //    public List<ItemStack> getStacksInSlots(int startSlot, int endSlot) {
    //
    //        if (startSlot < 0 || endSlot >= slots.size()) {
    //            return Collections.emptyList();
    //        }
    //        if (startSlot > endSlot) {
    //            int temp = endSlot;
    //            endSlot = startSlot;
    //            startSlot = temp;
    //        }
    //        ArrayList<ItemStack> ret = new ArrayList<>();
    //        for (int i = startSlot; i <= endSlot; ++i) {
    //            ret.add(get(i));
    //        }
    //        return ret;
    //    }

    public ItemStorageCoFH getSlot(int slot) {

        return slots.get(slot);
    }

    // region NBT
    public SimpleItemInv read(CompoundNBT nbt) {

        for (ItemStorageCoFH slot : slots) {
            slot.setItemStack(ItemStack.EMPTY);
        }
        ListNBT list = nbt.getList(tag, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundNBT slotTag = list.getCompound(i);
            int slot = slotTag.getByte(TAG_SLOT);
            if (slot >= 0 && slot < slots.size()) {
                slots.get(slot).read(slotTag);
            }
        }
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        if (slots.size() <= 0) {
            return nbt;
        }
        ListNBT list = new ListNBT();
        for (int i = 0; i < slots.size(); ++i) {
            if (!slots.get(i).isEmpty()) {
                CompoundNBT slotTag = new CompoundNBT();
                slotTag.putByte(TAG_SLOT, (byte) i);
                slots.get(i).write(slotTag);
                list.add(slotTag);
            }
        }
        if (!list.isEmpty()) {
            nbt.put(tag, list);
        }
        return nbt;
    }
    // endregion

    // HELPERS
    public CompoundNBT writeSlotsToNBT(CompoundNBT nbt, int startIndex, int endIndex) {

        return writeSlotsToNBT(nbt, tag, startIndex, endIndex);
    }

    public CompoundNBT writeSlotsToNBT(CompoundNBT nbt, String saveTag, int startIndex) {

        return writeSlotsToNBT(nbt, saveTag, startIndex, slots.size());
    }

    public CompoundNBT writeSlotsToNBT(CompoundNBT nbt, String saveTag, int startIndex, int endIndex) {

        if (startIndex < 0 || startIndex >= endIndex || startIndex >= slots.size()) {
            return nbt;
        }
        ListNBT list = new ListNBT();
        for (int i = startIndex; i < Math.min(endIndex, slots.size()); ++i) {
            if (!slots.get(i).isEmpty()) {
                CompoundNBT slotTag = new CompoundNBT();
                slotTag.putByte(TAG_SLOT, (byte) i);
                slots.get(i).write(slotTag);
                list.add(slotTag);
            }
        }
        if (!list.isEmpty()) {
            nbt.put(saveTag, list);
        }
        return nbt;
    }
    // endregion

    // UNORDERED METHODS
    public SimpleItemInv readSlotsUnordered(ListNBT list, int startIndex) {

        return readSlotsUnordered(list, startIndex, slots.size());
    }

    public SimpleItemInv readSlotsUnordered(ListNBT list, int startIndex, int endIndex) {

        if (startIndex < 0 || startIndex >= endIndex || startIndex >= slots.size()) {
            return this;
        }
        for (int i = 0; i < Math.min(Math.min(endIndex, slots.size()) - startIndex, list.size()); ++i) {
            CompoundNBT slotTag = list.getCompound(i);
            slots.get(startIndex + i).read(slotTag);
        }
        return this;
    }

    public CompoundNBT writeSlotsToNBTUnordered(CompoundNBT nbt, int startIndex, int endIndex) {

        return writeSlotsToNBTUnordered(nbt, tag, startIndex, endIndex);
    }

    public CompoundNBT writeSlotsToNBTUnordered(CompoundNBT nbt, String saveTag, int startIndex) {

        return writeSlotsToNBTUnordered(nbt, saveTag, startIndex, slots.size());
    }

    public CompoundNBT writeSlotsToNBTUnordered(CompoundNBT nbt, String saveTag, int startIndex, int endIndex) {

        if (startIndex < 0 || startIndex >= endIndex || startIndex >= slots.size()) {
            return nbt;
        }
        ListNBT list = new ListNBT();
        for (int i = startIndex; i < Math.min(endIndex, slots.size()); ++i) {
            if (!slots.get(i).isEmpty()) {
                CompoundNBT slotTag = new CompoundNBT();
                slots.get(i).write(slotTag);
                list.add(slotTag);
            }
        }
        if (!list.isEmpty()) {
            nbt.put(saveTag, list);
        }
        return nbt;
    }
    // endregion
}
