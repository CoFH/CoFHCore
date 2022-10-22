package cofh.core.util.helpers;

import cofh.core.util.filter.EmptyFilter;
import cofh.core.util.filter.IFilter;
import cofh.core.util.filter.IFilterableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import static cofh.core.util.helpers.AugmentableHelper.getPropertyWithDefault;
import static cofh.lib.util.constants.NBTTags.TAG_FILTER_TYPE;

public class FilterHelper {

    private FilterHelper() {

    }

    public static boolean hasFilter(ItemStack stack) {

        return !getFilterType(stack).isEmpty();
    }

    public static String getFilterType(ItemStack stack) {

        return getPropertyWithDefault(stack, TAG_FILTER_TYPE, "");
    }

    public static boolean hasFilter(IFilterableTile filterable) {

        IFilter filter = filterable.getFilter();
        return filter != null && filter != EmptyFilter.INSTANCE;
    }

    public static void openHeldGui(ServerPlayer player, MenuProvider containerSupplier) {

        NetworkHooks.openGui(player, containerSupplier, buf -> {
            buf.writeBoolean(true);
            buf.writeBlockPos(BlockPos.ZERO);
        });
    }

    public static void openTileGui(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos) {

        NetworkHooks.openGui(player, containerSupplier, buf -> {
            buf.writeBoolean(false);
            buf.writeBlockPos(pos);
        });
    }

}

