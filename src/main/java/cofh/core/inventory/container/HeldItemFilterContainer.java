package cofh.core.inventory.container;

import cofh.core.network.packet.server.ContainerPacket;
import cofh.core.util.filter.AbstractItemFilter;
import cofh.lib.inventory.container.ContainerCoFH;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.inventory.container.slot.SlotLocked;
import cofh.lib.inventory.wrapper.InvWrapperGeneric;
import cofh.lib.util.filter.IFilterOptions;
import cofh.lib.util.filter.IFilterableItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import static cofh.lib.util.references.CoreReferences.HELD_ITEM_FILTER_CONTAINER;

public class HeldItemFilterContainer extends ContainerCoFH implements IFilterOptions {

    protected final IFilterableItem filterable;
    protected AbstractItemFilter filter;
    protected InvWrapperGeneric filterInventory;
    protected ItemStack filterStack;

    public SlotLocked lockedSlot;

    public HeldItemFilterContainer(int windowId, PlayerInventory inventory, PlayerEntity player) {

        super(HELD_ITEM_FILTER_CONTAINER, windowId, inventory, player);

        allowSwap = false;

        filterStack = player.getMainHandItem();
        filterable = (IFilterableItem) filterStack.getItem();
        filter = (AbstractItemFilter) filterable.getFilter(filterStack);

        int slots = filter.size();
        filterInventory = new InvWrapperGeneric(this, filter.getItems(), slots);

        int rows = MathHelper.clamp(slots / 3, 1, 3);
        int rowSize = slots / rows;

        int xOffset = 62 - 9 * rowSize;
        int yOffset = 44 - 9 * rows;

        for (int i = 0; i < filter.size(); ++i) {
            addSlot(new SlotFalseCopy(filterInventory, i, xOffset + i % rowSize * 18, yOffset + i / rowSize * 18));
        }
        bindPlayerInventory(inventory);
    }

    @Override
    protected void bindPlayerInventory(PlayerInventory inventory) {

        int xOffset = getPlayerInventoryHorizontalOffset();
        int yOffset = getPlayerInventoryVerticalOffset();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            if (i == inventory.selected) {
                lockedSlot = new SlotLocked(inventory, i, xOffset + i * 18, yOffset + 58);
                addSlot(lockedSlot);
            } else {
                addSlot(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
            }
        }
    }

    public int getFilterSize() {

        return filter.size();
    }

    @Override
    protected int getMergeableSlotCount() {

        return filterInventory.getContainerSize();
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {

        return true;
    }

    @Override
    public void removed(PlayerEntity playerIn) {

        filter.setItems(filterInventory.getStacks());
        filter.write(filterStack.getOrCreateTag());
        filterable.onFilterChanged(filterStack);
        super.removed(playerIn);
    }

    // region NETWORK
    @Override
    public PacketBuffer getContainerPacket(PacketBuffer buffer) {

        buffer.writeBoolean(getAllowList());
        buffer.writeBoolean(getCheckNBT());

        return buffer;
    }

    @Override
    public void handleContainerPacket(PacketBuffer buffer) {

        filter.setAllowList(buffer.readBoolean());
        filter.setCheckNBT(buffer.readBoolean());
    }
    // endregion

    // region IFilterOptions
    @Override
    public boolean getAllowList() {

        return filter.getAllowList();
    }

    @Override
    public boolean setAllowList(boolean allowList) {

        boolean ret = filter.setAllowList(allowList);
        ContainerPacket.sendToServer(this);
        return ret;
    }

    @Override
    public boolean getCheckNBT() {

        return filter.getCheckNBT();
    }

    @Override
    public boolean setCheckNBT(boolean checkNBT) {

        boolean ret = filter.setCheckNBT(checkNBT);
        ContainerPacket.sendToServer(this);
        return ret;
    }
    // endregion
}
