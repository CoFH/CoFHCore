package cofh.lib.inventory.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Slot which players can only remove items from.
 */
public class SlotRemoveOnly extends SlotCoFH {

    public SlotRemoveOnly(Container inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return false;
    }

}
