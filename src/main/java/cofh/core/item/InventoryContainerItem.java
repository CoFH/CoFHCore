package cofh.core.item;

import cofh.core.inventory.IInventoryContainerItem;
import cofh.core.inventory.ItemStorageCoFH;
import cofh.core.inventory.SimpleItemInv;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.WeakHashMap;

import static cofh.lib.util.NBTTags.TAG_ITEM_INV;
import static cofh.core.util.helpers.ItemHelper.areItemStacksEqualIgnoreTags;

public class InventoryContainerItem extends ItemCoFH implements IInventoryContainerItem {

    protected static final int MAP_CAPACITY = 128;
    protected static final WeakHashMap<ItemStack, SimpleItemInv> INVENTORIES = new WeakHashMap<>(MAP_CAPACITY);

    protected int slots;

    public InventoryContainerItem(Properties builder, int slots) {

        super(builder);
        this.slots = slots;
    }

    protected SimpleItemInv readInventoryFromNBT(ItemStack container) {

        CompoundTag containerTag = getOrCreateInvTag(container);
        int numSlots = getContainerSlots(container);
        ArrayList<ItemStorageCoFH> invSlots = new ArrayList<>(numSlots);
        for (int i = 0; i < numSlots; ++i) {
            invSlots.add(new ItemStorageCoFH());
        }
        SimpleItemInv inventory = new SimpleItemInv(invSlots);
        inventory.read(containerTag);
        return inventory;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && slotChanged || !areItemStacksEqualIgnoreTags(oldStack, newStack, TAG_ITEM_INV);
    }

    // region IInventoryContainerItem
    @Override
    public SimpleItemInv getContainerInventory(ItemStack container) {

        SimpleItemInv ret = INVENTORIES.get(container);

        if (ret != null) {
            return ret;
        }
        if (INVENTORIES.size() > MAP_CAPACITY) {
            INVENTORIES.clear();
        }
        INVENTORIES.put(container, readInventoryFromNBT(container));
        return INVENTORIES.get(container);
    }

    @Override
    public int getContainerSlots(ItemStack container) {

        return slots;
    }

    @Override
    public void onContainerInventoryChanged(ItemStack container) {

        INVENTORIES.remove(container);
    }
    // endregion
}
