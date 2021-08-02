package cofh.lib.util.filter;

import net.minecraft.item.ItemStack;

public interface IFilterableItem {

    IFilter getFilter(ItemStack stack);

    void onFilterChanged(ItemStack stack);

}
