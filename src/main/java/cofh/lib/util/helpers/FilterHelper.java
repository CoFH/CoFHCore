package cofh.lib.util.helpers;

import cofh.core.util.filter.EmptyFilter;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterableTile;
import net.minecraft.item.ItemStack;

import static cofh.lib.util.constants.NBTTags.TAG_FILTER_TYPE;
import static cofh.lib.util.helpers.AugmentableHelper.getPropertyWithDefault;

public class FilterHelper {

    private FilterHelper() {

    }

    public static boolean hasFilter(ItemStack stack) {

        return !getFilterType(stack).isEmpty();
    }

    public static String getFilterType(ItemStack stack) {

        return getPropertyWithDefault(stack, TAG_FILTER_TYPE, "");
    }

    public static boolean hasFilter(IFilterableTile filterable) {

        IFilter filter = filterable.getFilter();
        return filter != null && filter != EmptyFilter.INSTANCE;
    }

}

