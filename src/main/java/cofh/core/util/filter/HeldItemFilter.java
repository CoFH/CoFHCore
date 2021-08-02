package cofh.core.util.filter;

import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.lib.util.filter.IFilter;
import cofh.lib.util.filter.IFilterFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

public class HeldItemFilter extends AbstractItemFilter {

    public static final IFilterFactory<IFilter> FACTORY = nbt -> new HeldItemFilter(SIZE).read(nbt);

    public HeldItemFilter(int size) {

        super(size);
    }

    // region INamedContainerProvider
    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {

        return new HeldItemFilterContainer(i, inventory, player);
    }
    // endregion
}
