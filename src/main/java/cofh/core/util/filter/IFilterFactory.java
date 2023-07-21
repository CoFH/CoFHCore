package cofh.core.util.filter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * Allows for retrieval and construction of a filter based on an NBT tag and associated parameters.
 */
public interface IFilterFactory<T extends IFilter> {

    T createFilter(CompoundTag nbt, FilterHolderType holderType, int id, BlockPos pos);

}
