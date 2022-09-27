package cofh.core.util.filter;

import cofh.core.inventory.container.HeldFluidFilterContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class HeldFluidFilter extends AbstractFluidFilter {

    public static final IFilterFactory<IFilter> FACTORY = nbt -> new HeldFluidFilter(SIZE).read(nbt);

    public HeldFluidFilter(int size) {

        super(size);
    }

    // region MenuProvider
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new HeldFluidFilterContainer(i, inventory, player);
    }
    // endregion
}
