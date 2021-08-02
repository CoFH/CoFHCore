package cofh.lib.inventory;

import cofh.lib.util.IResourceStorage;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.helpers.ItemHelper.cloneStack;
import static cofh.lib.util.helpers.ItemHelper.itemsEqualWithTags;

/**
 * Implementation of an Item Storage object.
 *
 * @author King Lemming
 */
public class ItemStorageCoFH implements IItemHandler, IItemStackAccess, IResourceStorage {

    protected final int baseCapacity;

    protected BooleanSupplier creative = FALSE;
    protected BooleanSupplier enabled = TRUE;
    protected Supplier<ItemStack> emptyItem = EMPTY_ITEM;
    protected Predicate<ItemStack> validator;

    @Nonnull
    protected ItemStack item = ItemStack.EMPTY;
    protected int capacity;

    public ItemStorageCoFH() {

        this(e -> true);
    }

    public ItemStorageCoFH(int capacity) {

        this(capacity, e -> true);
    }

    public ItemStorageCoFH(Predicate<ItemStack> validator) {

        this(0, validator);
    }

    public ItemStorageCoFH(int capacity, Predicate<ItemStack> validator) {

        this.baseCapacity = capacity;
        this.capacity = capacity;
        this.validator = validator;
    }

    public ItemStorageCoFH applyModifiers(float storageMod) {

        if (baseCapacity > 0) {
            setCapacity(Math.round(baseCapacity * storageMod));
        }
        return this;
    }

    public ItemStorageCoFH setCapacity(int capacity) {

        this.capacity = capacity;
        return this;
    }

    public ItemStorageCoFH setEmptyItem(Supplier<ItemStack> emptyItemSupplier) {

        if (emptyItemSupplier != null && emptyItemSupplier.get() != null) {
            this.emptyItem = emptyItemSupplier;
        }
        if (item.isEmpty()) {
            setItemStack(this.emptyItem.get());
        }
        return this;
    }

    public ItemStorageCoFH setCreative(BooleanSupplier creative) {

        this.creative = creative;
        if (!item.isEmpty() && isCreative()) {
            item.setCount(getCapacity());
        }
        return this;
    }

    public ItemStorageCoFH setEnabled(BooleanSupplier enabled) {

        if (enabled != null) {
            this.enabled = enabled;
        }
        return this;
    }

    public ItemStorageCoFH setValidator(Predicate<ItemStack> validator) {

        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isItemValid(@Nonnull ItemStack stack) {

        return enabled.getAsBoolean() && validator.test(stack);
    }

    public void consume(int amount) {

        this.item = ItemHelper.consumeItem(item, amount);
    }

    public void setItemStack(ItemStack item) {

        this.item = item.isEmpty() ? emptyItem.get() : item;
    }

    // region NBT
    public ItemStorageCoFH read(CompoundNBT nbt) {

        item = ItemStack.read(nbt);
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        item.write(nbt);
        return nbt;
    }
    // endregion

    // region IItemStackAccess
    @Override
    public ItemStack getItemStack() {

        return item;
    }

    @Override
    public int getCount() {

        return item.getCount();
    }

    @Override
    public boolean isEmpty() {

        return item.isEmpty();
    }

    @Override
    public boolean isFull() {

        return item.getCount() >= getCapacity();
    }
    // endregion

    // region IItemHandler
    @Override
    public int getSlots() {

        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {

        return item;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!isItemValid(stack) || !enabled.getAsBoolean()) {
            return stack;
        }
        if (item.isEmpty()) {
            if (!simulate) {
                setItemStack(stack);
            }
            return ItemStack.EMPTY;
        } else if (itemsEqualWithTags(item, stack)) {
            int totalCount = item.getCount() + stack.getCount();
            int limit = getSlotLimit(0);
            if (totalCount <= limit) {
                if (!simulate) {
                    item.setCount(totalCount);
                }
                return ItemStack.EMPTY;
            }
            if (!simulate) {
                item.setCount(limit);
            }
            return cloneStack(stack, totalCount - limit);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (amount <= 0 || item.isEmpty() || !enabled.getAsBoolean()) {
            return ItemStack.EMPTY;
        }
        int retCount = Math.min(item.getCount(), amount);
        ItemStack ret = cloneStack(item, retCount);
        if (!simulate && !isCreative()) {
            item.shrink(retCount);
            if (item.isEmpty()) {
                setItemStack(emptyItem.get());
            }
        }
        return ret;
    }

    @Override
    public int getSlotLimit(int slot) {

        return capacity <= 0 ? item.getMaxStackSize() : capacity;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

        return isItemValid(stack);
    }
    // endregion

    // region IResourceStorage
    @Override
    public boolean clear() {

        if (isEmpty()) {
            return false;
        }
        this.item = emptyItem.get();
        return true;
    }

    @Override
    public void modify(int quantity) {

        if (isCreative()) {
            quantity = Math.max(quantity, 0);
        }
        this.item.setCount(Math.min(item.getCount() + quantity, getCapacity()));
        if (this.item.isEmpty()) {
            this.item = emptyItem.get();
        }
    }

    @Override
    public boolean isCreative() {

        return creative.getAsBoolean();
    }

    @Override
    public int getCapacity() {

        return getSlotLimit(0);
    }

    @Override
    public int getStored() {

        return item.getCount();
    }

    @Override
    public String getUnit() {

        return "";
    }
    // endregion
}
