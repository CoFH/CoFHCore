package cofh.lib.util.filter;

import net.minecraft.nbt.CompoundTag;

/**
 * Allows for retrieval and construction of a filter based on nothing but an NBT tag.
 */
public interface IFilterFactory<T extends IFilter> {

    T createFilter(CompoundTag nbt);

}
