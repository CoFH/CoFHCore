package cofh.core.util.filter;

import cofh.core.content.inventory.container.TileItemFilterContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class TileItemFilter extends AbstractItemFilter {

    public static final ITileFilterFactory<IFilter> FACTORY = (tile, nbt) -> new TileItemFilter(tile, SIZE).read(nbt);

    protected final IFilterableTile tile;

    public TileItemFilter(IFilterableTile tile, int size) {

        super(size);
        this.tile = tile;
    }

    // region MenuProvider
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new TileItemFilterContainer(i, tile.world(), tile.pos(), inventory, player);
    }
    // endregion
}
