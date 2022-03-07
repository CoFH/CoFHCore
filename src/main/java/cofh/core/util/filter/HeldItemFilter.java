package cofh.core.util.filter;

import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterFactory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class HeldItemFilter extends AbstractItemFilter {

    public static final IFilterFactory<IFilter> FACTORY = nbt -> new HeldItemFilter(SIZE).read(nbt);

    public HeldItemFilter(int size) {

        super(size);
    }

    // region INamedContainerProvider
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new HeldItemFilterContainer(i, inventory, player);
    }
    // endregion
}
