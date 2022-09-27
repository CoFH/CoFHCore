package cofh.core.util.filter;

import cofh.core.inventory.container.TileFluidFilterContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class TileFluidFilter extends AbstractFluidFilter {

    public static final ITileFilterFactory<IFilter> FACTORY = (tile, nbt) -> new TileFluidFilter(tile, SIZE).read(nbt);

    protected final IFilterableTile tile;

    public TileFluidFilter(IFilterableTile tile, int size) {

        super(size);
        this.tile = tile;
    }

    // region MenuProvider
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new TileFluidFilterContainer(i, tile.world(), tile.pos(), inventory, player);
    }
    // endregion
}
