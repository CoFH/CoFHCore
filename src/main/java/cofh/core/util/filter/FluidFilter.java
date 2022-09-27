package cofh.core.util.filter;

import cofh.core.inventory.container.FluidFilterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FluidFilter extends AbstractFluidFilter {

    public static final IFilterFactory<IFilter> FACTORY = (nbt, held, pos, filterId) -> new FluidFilter(SIZE, held, pos, filterId).read(nbt);

    protected final boolean held;
    protected final BlockPos pos;
    protected final int filterId;

    public FluidFilter(int size, boolean held, BlockPos pos, int filterId) {

        super(size);
        this.held = held;
        this.pos = pos;
        this.filterId = filterId;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new FluidFilterContainer(i, player.level, inventory, player, held, pos, filterId);
    }

}
