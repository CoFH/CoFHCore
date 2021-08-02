package cofh.core.util.filter;

import cofh.core.inventory.container.TileItemFilterContainer;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterableTile;
import cofh.lib.util.filter.ITileFilterFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

public class TileItemFilter extends AbstractItemFilter {

    public static final ITileFilterFactory<IFilter> FACTORY = (tile, nbt) -> new TileItemFilter(tile, SIZE).read(nbt);

    protected final IFilterableTile tile;

    public TileItemFilter(IFilterableTile tile, int size) {

        super(size);
        this.tile = tile;
    }

    // region INamedContainerProvider
    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {

        return new TileItemFilterContainer(i, tile.world(), tile.pos(), inventory, player);
    }
    // endregion
}
