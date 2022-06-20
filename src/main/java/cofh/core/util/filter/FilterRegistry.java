package cofh.core.util.filter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public class FilterRegistry {

    public static final String EMPTY_FILTER_TYPE = "";
    public static final String DUAL_FILTER_TYPE = "dual";
    public static final String FLUID_FILTER_TYPE = "fluid";
    public static final String ITEM_FILTER_TYPE = "item";

    protected static final Map<String, IFilterFactory<? extends IFilter>> HELD_FILTER_MAP = new Object2ObjectOpenHashMap<>();
    protected static final Map<String, ITileFilterFactory<? extends IFilter>> TILE_FILTER_MAP = new Object2ObjectOpenHashMap<>();

    static {
        registerHeldFilter(ITEM_FILTER_TYPE, HeldItemFilter.FACTORY);
        registerTileFilter(ITEM_FILTER_TYPE, TileItemFilter.FACTORY);
    }

    public static boolean registerHeldFilter(String type, IFilterFactory<?> factory) {

        if (type == null || type.isEmpty() || factory == null) {
            return false;
        }
        HELD_FILTER_MAP.put(type, factory);
        return true;
    }

    public static boolean registerTileFilter(String type, ITileFilterFactory<?> factory) {

        if (type == null || type.isEmpty() || factory == null) {
            return false;
        }
        TILE_FILTER_MAP.put(type, factory);
        return true;
    }

    public static IFilter getHeldFilter(String type, CompoundTag nbt) {

        if (HELD_FILTER_MAP.containsKey(type)) {
            return HELD_FILTER_MAP.get(type).createFilter(nbt);
        }
        return EmptyFilter.INSTANCE;
    }

    public static IFilter getTileFilter(String type, IFilterableTile tile, CompoundTag nbt) {

        if (TILE_FILTER_MAP.containsKey(type)) {
            return TILE_FILTER_MAP.get(type).createFilter(tile, nbt);
        }
        return EmptyFilter.INSTANCE;
    }

}
