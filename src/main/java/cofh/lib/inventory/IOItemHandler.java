package cofh.lib.inventory;

import cofh.lib.api.IStorageCallback;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.lib.util.Constants.TRUE;

public class IOItemHandler extends SimpleItemHandler {

    protected BooleanSupplier allowInsert = TRUE;
    protected BooleanSupplier allowExtract = TRUE;

    public IOItemHandler(@Nullable IStorageCallback callback, @Nonnull List<ItemStorageCoFH> slots) {

        super(callback, slots);
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
