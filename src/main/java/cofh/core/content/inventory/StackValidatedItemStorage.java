package cofh.core.content.inventory;

import java.util.function.BooleanSupplier;

import static cofh.core.util.helpers.ItemHelper.itemsEqual;
import static cofh.core.util.helpers.ItemHelper.itemsEqualWithTags;
import static cofh.lib.util.Constants.TRUE;

public class StackValidatedItemStorage extends ItemStorageCoFH {

    protected final IItemStackAccess linkedStack;
    protected BooleanSupplier checkNBT = TRUE;

    public StackValidatedItemStorage(IItemStackAccess linkedStack) {

        this.linkedStack = linkedStack;

        validator = (item) -> checkNBT.getAsBoolean() ? itemsEqualWithTags(item, linkedStack.getItemStack()) : itemsEqual(item, linkedStack.getItemStack());
    }

    public StackValidatedItemStorage setCheckNBT(BooleanSupplier checkNBT) {

        this.checkNBT = checkNBT;
        return this;
    }

    @Override
    public int getSlotLimit(int slot) {

        return linkedStack.getCount();
    }

}