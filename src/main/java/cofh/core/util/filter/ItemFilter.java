package cofh.core.util.filter;

import cofh.core.inventory.container.ItemFilterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class ItemFilter extends BaseItemFilter implements MenuProvider {

    public static final Component DISPLAY_NAME = new TranslatableComponent("info.cofh.item_filter");
    public static final IFilterFactory<IFilter> FACTORY = (nbt, held, pos) -> new ItemFilter(SIZE, held, pos).read(nbt);

    protected final boolean held;
    protected final BlockPos pos;

    public ItemFilter(int size, boolean held, BlockPos pos) {

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

        return new ItemFilterContainer(i, player.level, inventory, player, held, pos);
    }

}
