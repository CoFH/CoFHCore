package cofh.lib.util.filter;

import net.minecraft.nbt.CompoundNBT;

/**
 * May feel a little clunky, but this ensures behavior that Tile-based filters are constructed with their tile reference.
 */
public interface ITileFilterFactory<T extends IFilter> {

    T createFilter(IFilterableTile tile, CompoundNBT nbt);

}
