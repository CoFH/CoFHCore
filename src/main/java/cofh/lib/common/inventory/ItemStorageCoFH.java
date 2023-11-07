package cofh.lib.common.inventory;

import cofh.core.util.helpers.ItemHelper;
import cofh.lib.api.IResourceStorage;
import cofh.lib.api.inventory.IItemStackHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static cofh.core.util.helpers.ItemHelper.cloneStack;
import static cofh.core.util.helpers.ItemHelper.itemsEqualWithTags;
import static cofh.lib.util.Constants.*;

/**
 * Implementation of an Item Storage object.
 *
 * @author King Lemming
 */
public class ItemStorageCoFH implements IItemHandler, IItemStackHolder, IResourceStorage {

    protected static Predicate<ItemStack> DEFAULT_VALIDATOR = e -> true;

    protected final int baseCapacity;

    protected Supplier<Boolean> creative = FALSE;
    protected Supplier<Boolean> enabled = TRUE;
    protected Supplier<ItemStack> emptyItem = EMPTY_ITEM;
    protected Predicate<ItemStack> validator;

    @Nonnull
    protected ItemStack item = ItemStack.EMPTY;
    protected int capacity;

    public ItemStorageCoFH() {

        this(DEFAULT_VALIDATOR);
    }

    public ItemStorageCoFH(int capacity) {

        this(capacity, DEFAULT_VALIDATOR);
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

    public ItemStorageCoFH setCreative(Supplier<Boolean> creative) {

        this.creative = creative;
        if (!item.isEmpty() && isCreative()) {
            item.setCount(getCapacity());
        }
        return this;
    }

    public ItemStorageCoFH setEnabled(Supplier<Boolean> enabled) {

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

        return enabled.get() && validator.test(stack);
    }

    public void consume(int amount) {

        this.item = ItemHelper.consumeItem(item, amount);
    }

    public void setItemStack(ItemStack item) {

        this.item = item.isEmpty() ? emptyItem.get() : item;
    }

    // region NBT
    public ItemStorageCoFH read(CompoundTag nbt) {

        item = loadItemStack(nbt);
        return this;
    }

    public CompoundTag write(CompoundTag nbt) {

        saveItemStack(item, nbt);
        return nbt;
    }
    // endregion

    public static ItemStack loadItemStack(CompoundTag nbt) {

        ItemStack retStack = ItemStack.of(nbt);
        if (nbt.contains("IntCount")) {
            int storedCount = nbt.getInt("IntCount");
            if (retStack.getCount() < storedCount) {
                retStack.setCount(storedCount);
            }
        }
        return retStack;
    }

    protected final void saveItemStack(ItemStack stack, CompoundTag nbt) {

        stack.save(nbt);
        if (stack.getCount() > Byte.MAX_VALUE) {
            nbt.putInt("IntCount", stack.getCount());
        }
    }

    // region IItemStackHolder
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
        if (!isItemValid(stack) || !enabled.get()) {
            return stack;
        }
        if (item.isEmpty()) {
            int maxStack = getSlotLimit(slot);
            int count = Math.min(stack.getCount(), maxStack);
            if (!simulate) {
                setItemStack(cloneStack(stack, count));
            }
            return count >= stack.getCount() ? ItemStack.EMPTY : cloneStack(stack, stack.getCount() - count);
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

        if (amount <= 0 || item.isEmpty() || !enabled.get()) {
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

        return creative.get();
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
