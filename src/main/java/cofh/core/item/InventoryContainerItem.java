package cofh.core.item;

import cofh.lib.inventory.IInventoryContainerItem;
import cofh.lib.inventory.SimpleItemInv;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class InventoryContainerItem extends ItemCoFH implements IInventoryContainerItem {

    protected static final int MAP_CAPACITY = 128;
    protected static final WeakHashMap<ItemStack, IItemHandler> INVENTORIES = new WeakHashMap<>(MAP_CAPACITY);

    protected int slots;

    public InventoryContainerItem(Properties builder, int slots) {

        super(builder);
        this.slots = slots;
    }

    protected IItemHandler readInventoryFromNBT(ItemStack container) {

        CompoundNBT containerTag = getOrCreateInvTag(container);
        SimpleItemInv inventory = new SimpleItemInv(new ArrayList<>(getSlots(container)));
        inventory.read(containerTag);

        return inventory;
    }

    // region IInventoryContainerItem
    @Override
    public IItemHandler getInventory(ItemStack container) {

        IItemHandler ret = INVENTORIES.get(container);
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
    public int getSlots(ItemStack container) {

        return slots;
    }

    @Override
    public void onInventoryChanged(ItemStack container) {

        INVENTORIES.remove(container);
    }
    // endregion
}
