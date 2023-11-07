package cofh.core.util.filter;

import cofh.core.common.inventory.FluidFilterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FluidFilter extends BaseFluidFilter implements MenuProvider {

    public static final Component DISPLAY_NAME = Component.translatable("info.cofh.fluid_filter");
    public static final IFilterFactory<IFilter> FACTORY = (nbt, holderType, id, pos) -> new FluidFilter(15, holderType, id, pos).read(nbt);

    protected final FilterHolderType holderType;
    protected final int id;
    protected final BlockPos pos;

    public FluidFilter(int size, FilterHolderType holderType, int id, BlockPos pos) {

        super(size);
        this.holderType = holderType;
        this.id = id;
        this.pos = pos;
    }

    @Override
    public Component getDisplayName() {

        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return new FluidFilterContainer(i, player.level, inventory, player, holderType.ordinal(), id, pos);
    }

}
