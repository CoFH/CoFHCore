package cofh.lib.inventory;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemStackAccess {

    @Nonnull
    ItemStack getItemStack();

    int getCount();

    boolean isEmpty();

    boolean isFull();

}
