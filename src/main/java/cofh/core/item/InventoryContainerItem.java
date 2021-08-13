package cofh.core.item;

import cofh.lib.inventory.IInventoryContainerItem;
import cofh.lib.inventory.ItemStorageCoFH;
import cofh.lib.inventory.SimpleItemInv;
import cofh.lib.util.IInventoryCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class InventoryContainerItem extends ItemCoFH implements IInventoryContainerItem {

    protected static final int MAP_CAPACITY = 64;
    protected static final WeakHashMap<ItemStack, SimpleItemInv> INVENTORIES = new WeakHashMap<>(MAP_CAPACITY);

    protected int slots;

    public InventoryContainerItem(Properties builder, int slots) {

        super(builder);
        this.slots = slots;
    }

    protected SimpleItemInv readInventoryFromNBT(ItemStack container) {

        CompoundNBT containerTag = getOrCreateInvTag(container);
        int numSlots = getContainerSlots(container);
        ArrayList<ItemStorageCoFH> invSlots = new ArrayList<>(numSlots);
        for (int i = 0; i < numSlots; ++i) {
            invSlots.add(new ItemStorageCoFH());
        }
        SimpleItemInv inventory = new SimpleItemInv(invSlots);
        inventory.read(containerTag);
        return inventory;
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

    // region CALLBACK
    public class ContainerCallback implements IInventoryCallback {

        private final ItemStack containerItem;

        public ContainerCallback(ItemStack containerItem) {

            this.containerItem = containerItem;
        }

        public void onInventoryChanged(int slot) {

            onContainerInventoryChanged(containerItem);
        }

    }
    // endregion
}
