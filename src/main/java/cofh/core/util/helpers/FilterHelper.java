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

    public static boolean hasFilter(IFilterableTile filterable, int id) {

        IFilter filter = filterable.getFilter(id);
        return filter != null && filter != EmptyFilter.INSTANCE;
    }

    public static void openHeldScreen(ServerPlayer player, MenuProvider containerSupplier) {

        NetworkHooks.openScreen(player, containerSupplier, buf -> {
            buf.writeBoolean(true);
            buf.writeBlockPos(BlockPos.ZERO);
            buf.writeByte(0);
        });
    }

    public static void openTileScreen(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos, int filterId) {

        NetworkHooks.openScreen(player, containerSupplier, buf -> {
            buf.writeBoolean(false);
            buf.writeBlockPos(pos);
            buf.writeByte(filterId);
        });
    }

}

