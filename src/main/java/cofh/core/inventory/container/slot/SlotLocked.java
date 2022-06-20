package cofh.core.inventory.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Slot which cannot be interacted with.
 */
public class SlotLocked extends SlotCoFH {

    public SlotLocked(Container inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player player) {

        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return false;
    }

}
