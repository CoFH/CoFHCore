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
    public boolean mayPickup(PlayerEntity player) {

        return false;
    }

    @Override
    public void set(ItemStack stack) {

        if (!mayPlace(stack)) {
            return;
        }
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }
        container.setItem(this.slot, stack);
        setChanged();
    }

}
