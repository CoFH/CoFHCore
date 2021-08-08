package cofh.lib.inventory;

import cofh.lib.util.IInventoryCallback;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;

public class IOItemHandler extends SimpleItemHandler {

    protected BooleanSupplier allowInsert = TRUE;
    protected BooleanSupplier allowExtract = TRUE;

    public IOItemHandler(@Nullable IInventoryCallback tile, @Nonnull List<ItemStorageCoFH> slots) {

        super(tile, slots);
    }

    public void setConditions(BooleanSupplier allowInsert, BooleanSupplier allowExtract) {

        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (!allowInsert.getAsBoolean()) {
            return ItemStack.EMPTY;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (!allowExtract.getAsBoolean()) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }

}
