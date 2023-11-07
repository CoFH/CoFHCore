package cofh.lib.common.inventory;

import cofh.lib.common.inventory.wrapper.InvWrapperCoFH;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class SlotCoFH extends Slot {

    protected IntSupplier slotStackLimit;
    protected Supplier<Boolean> enabled = TRUE;

    public SlotCoFH(InvWrapperCoFH inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
        slotStackLimit = () -> inventoryIn.getSlotLimit(index);
    }

    public SlotCoFH(Container inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
        slotStackLimit = () -> inventoryIn.getMaxStackSize();
    }

    public SlotCoFH(Container inventoryIn, int index, int xPosition, int yPosition, int slotStackLimit) {

        super(inventoryIn, index, xPosition, yPosition);
        this.slotStackLimit = () -> slotStackLimit;
    }

    public SlotCoFH(Container inventoryIn, int index, int xPosition, int yPosition, IntSupplier slotStackLimit) {

        super(inventoryIn, index, xPosition, yPosition);
        this.slotStackLimit = slotStackLimit;
    }

    public SlotCoFH setEnabled(Supplier<Boolean> enabled) {

        this.enabled = enabled;
        return this;
    }

    @Override
    public int getMaxStackSize() {

        return slotStackLimit.getAsInt();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return container.canPlaceItem(slot, stack);
    }

    @Override
    public boolean isActive() {

        return enabled.get();
    }

}
