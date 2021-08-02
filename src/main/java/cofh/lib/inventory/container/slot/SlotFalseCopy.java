package cofh.lib.inventory.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Slot which copies an ItemStack when clicked on (and sets it to stacksize 1), does not decrement the ItemStack on the cursor.
 */
public class SlotFalseCopy extends SlotCoFH {

    public SlotFalseCopy(IInventory inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {

        return false;
    }

    @Override
    public void putStack(ItemStack stack) {

        if (!isItemValid(stack)) {
            return;
        }
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }
        inventory.setInventorySlotContents(this.slotIndex, stack);
        onSlotChanged();
    }

}
