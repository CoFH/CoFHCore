package cofh.core.util.filter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public class FilterRegistry {

    public static final String FLUID_FILTER_TYPE = "fluid";
    public static final String ITEM_FILTER_TYPE = "item";

    protected static final Map<String, IFilterFactory<? extends IFilter>> FILTER_FACTORY_MAP = new Object2ObjectOpenHashMap<>();

    static {
        registerFilterFactory(FLUID_FILTER_TYPE, FluidFilter.FACTORY);
        registerFilterFactory(ITEM_FILTER_TYPE, ItemFilter.FACTORY);
    }

    public static boolean registerFilterFactory(String type, IFilterFactory<?> factory) {

        if (type == null || type.isEmpty() || factory == null) {
            return false;
        }
        FILTER_FACTORY_MAP.put(type, factory);
        return true;
    }

    public static IFilter getHeldFilter(String type, CompoundTag nbt) {

        if (FILTER_FACTORY_MAP.containsKey(type)) {
            return FILTER_FACTORY_MAP.get(type).createFilter(nbt, true, BlockPos.ZERO, (byte) 0);
        }
        return EmptyFilter.INSTANCE;
    }

    public static IFilter getTileFilter(String type, CompoundTag nbt, IFilterableTile tile, byte filterId) {

        if (FILTER_FACTORY_MAP.containsKey(type)) {
            return FILTER_FACTORY_MAP.get(type).createFilter(nbt, false, tile.pos(), filterId);
        }
        return EmptyFilter.INSTANCE;
    }

}
