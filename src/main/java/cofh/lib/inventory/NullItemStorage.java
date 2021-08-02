package cofh.lib.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class NullItemStorage extends ItemStorageCoFH {

    public NullItemStorage() {

        super();
    }

    public NullItemStorage(Predicate<ItemStack> validator) {

        super(validator);
    }

    @Override
    public void consume(int amount) {

        // Do Nothing
    }

    @Override
    public void setItemStack(ItemStack item) {

        // Do Nothing
    }

    // region IItemHandler
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (!isItemValid(stack) || !enabled.getAsBoolean()) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        return ItemStack.EMPTY;
    }
    // endregion
}
