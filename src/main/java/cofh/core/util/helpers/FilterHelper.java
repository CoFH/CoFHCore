package cofh.core.util.helpers;

import cofh.core.util.filter.EmptyFilter;
import cofh.core.util.filter.IFilter;
import cofh.core.util.filter.IFilterableTile;
import net.minecraft.world.item.ItemStack;

import static cofh.core.util.helpers.AugmentableHelper.getPropertyWithDefault;
import static cofh.lib.util.constants.NBTTags.TAG_FILTER_TYPE;

public class FilterHelper {

    private FilterHelper() {

    }

    public static boolean hasFilter(ItemStack stack) {

        return !getFilterType(stack).isEmpty();
    }

    public static String getFilterType(ItemStack stack) {

        return getPropertyWithDefault(stack, TAG_FILTER_TYPE, "");
    }

    public static boolean hasFilter(IFilterableTile filterable, int id) {

        IFilter filter = filterable.getFilter(id);
        return filter != null && filter != EmptyFilter.INSTANCE;
    }

}

