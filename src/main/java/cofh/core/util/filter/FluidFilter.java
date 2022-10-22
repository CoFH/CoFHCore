package cofh.core.util.filter;

import cofh.core.inventory.container.FluidFilterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FluidFilter extends BaseFluidFilter implements MenuProvider {

    public static final Component DISPLAY_NAME = new TranslatableComponent("info.cofh.fluid_filter");
    public static final IFilterFactory<IFilter> FACTORY = (nbt, held, pos) -> new FluidFilter(SIZE, held, pos).read(nbt);

    protected final boolean held;
    protected final BlockPos pos;

    public FluidFilter(int size, boolean held, BlockPos pos) {

        super(size);
        this.held = held;
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {

        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new FluidFilterContainer(i, player.level, inventory, player, held, pos);
    }

}
