package cofh.lib.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackHolder implements IItemStackAccess {

    protected ItemStack stack;

    public ItemStackHolder(ItemStack stack) {

        this.stack = stack;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {

        return stack;
    }

    @Override
    public int getCount() {

        return stack.getCount();
    }

    @Override
    public boolean isEmpty() {

        return stack.isEmpty();
    }

    @Override
    public boolean isFull() {

        return stack.getCount() >= stack.getMaxStackSize();
    }

}
