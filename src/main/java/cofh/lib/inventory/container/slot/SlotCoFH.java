package cofh.lib.inventory.container.slot;

import cofh.lib.inventory.wrapper.InvWrapperCoFH;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import static cofh.lib.util.Constants.TRUE;

public class SlotCoFH extends Slot {

    protected IntSupplier slotStackLimit;
    protected BooleanSupplier enabled = TRUE;

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

    public SlotCoFH setEnabled(BooleanSupplier enabled) {

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

    @OnlyIn (Dist.CLIENT)
    @Override
    public boolean isActive() {

        return enabled.getAsBoolean();
    }

}
