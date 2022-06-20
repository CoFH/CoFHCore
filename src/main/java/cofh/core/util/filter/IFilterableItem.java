package cofh.core.util.filter;

import net.minecraft.world.item.ItemStack;

public interface IFilterableItem {

    IFilter getFilter(ItemStack stack);

    void onFilterChanged(ItemStack stack);

}
