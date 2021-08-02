package cofh.lib.inventory.container.slot;

import cofh.lib.inventory.wrapper.InvWrapperCoFH;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import static cofh.lib.util.constants.Constants.TRUE;

public class SlotCoFH extends Slot {

    protected IntSupplier slotStackLimit;
    protected BooleanSupplier enabled = TRUE;

    public SlotCoFH(InvWrapperCoFH inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
        slotStackLimit = () -> inventoryIn.getSlotLimit(index);
    }

    public SlotCoFH(IInventory inventoryIn, int index, int xPosition, int yPosition) {

        super(inventoryIn, index, xPosition, yPosition);
        slotStackLimit = () -> inventoryIn.getInventoryStackLimit();
    }

    public SlotCoFH(IInventory inventoryIn, int index, int xPosition, int yPosition, int slotStackLimit) {

        super(inventoryIn, index, xPosition, yPosition);
        this.slotStackLimit = () -> slotStackLimit;
    }

    public SlotCoFH setEnabled(BooleanSupplier enabled) {

        this.enabled = enabled;
        return this;
    }

    @Override
    public int getSlotStackLimit() {

        return slotStackLimit.getAsInt();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {

        return inventory.isItemValidForSlot(slotIndex, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isEnabled() {

        return enabled.getAsBoolean();
    }

}
