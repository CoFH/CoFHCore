package cofh.core.inventory.container;

import cofh.lib.inventory.IInventoryContainerItem;
import cofh.lib.inventory.SimpleItemInv;
import cofh.lib.inventory.container.ContainerCoFH;
import cofh.lib.inventory.container.slot.SlotCoFH;
import cofh.lib.inventory.container.slot.SlotLocked;
import cofh.lib.inventory.wrapper.InvWrapperCoFH;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import static cofh.lib.util.references.CoreReferences.HELD_INVENTORY_CONTAINER;

public class HeldInventoryContainer extends ContainerCoFH {

    protected final IInventoryContainerItem containerItem;
    protected SimpleItemInv itemInventory;
    protected InvWrapperCoFH invWrapper;
    protected ItemStack containerStack;

    protected int slots;
    protected int rows;

    public HeldInventoryContainer(int windowId, PlayerInventory inventory, PlayerEntity player) {

        super(HELD_INVENTORY_CONTAINER, windowId, inventory, player);

        allowSwap = false;

        containerStack = player.getHeldItemMainhand();
        containerItem = (IInventoryContainerItem) containerStack.getItem();

        slots = containerItem.getContainerSlots(containerStack);
        itemInventory = containerItem.getContainerInventory(containerStack);
        invWrapper = new InvWrapperCoFH(itemInventory, slots);

        rows = MathHelper.clamp(slots / 9, 0, 10);
        int extraSlots = slots % 9;

        int xOffset = 8;
        int yOffset = 44 - 9 * MathHelper.clamp(rows + (extraSlots > 0 ? 1 : 0), 0, 3);

        for (int i = 0; i < rows * 9; ++i) {
            addSlot(new SlotCoFH(invWrapper, i, xOffset + i % 9 * 18, yOffset + i / 9 * 18));
        }
        if (extraSlots > 0) {
            xOffset = 89 - 9 * extraSlots;
            for (int i = slots - extraSlots; i < slots; ++i) {
                addSlot(new SlotCoFH(invWrapper, i, xOffset + i % extraSlots * 18, yOffset + 18 * rows));
            }
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
            if (i == inventory.currentItem) {
                addSlot(new SlotLocked(inventory, i, xOffset + i * 18, yOffset + 58));
            } else {
                addSlot(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
            }
        }
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {

        return 84 + MathHelper.clamp(rows - 3, 0, 7) * 18;
    }

    public int getContainerInventorySize() {

        return slots;
    }

    @Override
    protected int getMergeableSlotCount() {

        return invWrapper.getSizeInventory();
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {

        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {

        itemInventory.write(containerItem.getOrCreateInvTag(containerStack));
        containerItem.onContainerInventoryChanged(containerStack);
        super.onContainerClosed(playerIn);
    }

}
