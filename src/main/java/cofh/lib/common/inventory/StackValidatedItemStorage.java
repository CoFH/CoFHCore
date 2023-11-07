package cofh.lib.common.inventory;

import cofh.lib.api.inventory.IItemStackHolder;

import java.util.function.Supplier;

import static cofh.core.util.helpers.ItemHelper.itemsEqual;
import static cofh.core.util.helpers.ItemHelper.itemsEqualWithTags;
import static cofh.lib.util.Constants.TRUE;

public class StackValidatedItemStorage extends ItemStorageCoFH {

    protected final IItemStackHolder linkedStack;
    protected Supplier<Boolean> checkNBT = TRUE;

    public StackValidatedItemStorage(IItemStackHolder linkedStack) {

        this.linkedStack = linkedStack;

        validator = (item) -> checkNBT.get() ? itemsEqualWithTags(item, linkedStack.getItemStack()) : itemsEqual(item, linkedStack.getItemStack());
    }

    public StackValidatedItemStorage setCheckNBT(Supplier<Boolean> checkNBT) {

        this.checkNBT = checkNBT;
        return this;
    }

    @Override
    public int getSlotLimit(int slot) {

        return linkedStack.getCount();
    }

}
