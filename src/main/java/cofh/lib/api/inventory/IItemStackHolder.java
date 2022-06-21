package cofh.lib.api.inventory;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemStackHolder {

    @Nonnull
    ItemStack getItemStack();

    int getCount();

    boolean isEmpty();

    boolean isFull();

}
