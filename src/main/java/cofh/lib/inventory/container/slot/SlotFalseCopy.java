package cofh.lib.inventory.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Slot which copies an ItemStack when clicked on (and sets it to stacksize 1), does not decrement the ItemStack on the cursor.
 */
public class SlotFalseCopy extends SlotCoFH {

    public SlotFalseCopy(Container inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player player) {

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
