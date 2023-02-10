package cofh.core.entity;

import net.minecraft.world.item.ItemStack;

public interface IOnPlaced {

    IOnPlaced onPlaced(ItemStack stack);

}
