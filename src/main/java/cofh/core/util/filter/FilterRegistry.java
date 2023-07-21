package cofh.core.util.filter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

import static cofh.core.util.filter.FilterHolderType.*;

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

    public static IFilter getFilter(String type, CompoundTag nbt) {

        if (FILTER_FACTORY_MAP.containsKey(type)) {
            return FILTER_FACTORY_MAP.get(type).createFilter(nbt, ITEM, -1, BlockPos.ZERO);
        }
        return EmptyFilter.INSTANCE;
    }

    public static IFilter getFilter(String type, CompoundTag nbt, BlockEntity tile) {

        if (FILTER_FACTORY_MAP.containsKey(type)) {
            return FILTER_FACTORY_MAP.get(type).createFilter(nbt, TILE, -1, tile.getBlockPos());
        }
        return EmptyFilter.INSTANCE;
    }

    public static IFilter getFilter(String type, CompoundTag nbt, Entity entity) {

        if (FILTER_FACTORY_MAP.containsKey(type)) {
            return FILTER_FACTORY_MAP.get(type).createFilter(nbt, ENTITY, entity.getId(), BlockPos.ZERO);
        }
        return EmptyFilter.INSTANCE;
    }

}
