package cofh.core.util.helpers;

import cofh.core.util.filter.EmptyFilter;
import cofh.core.util.filter.FilterHolderType;
import cofh.core.util.filter.IFilter;
import cofh.core.util.filter.IFilterable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

import static cofh.core.util.helpers.AugmentableHelper.getPropertyWithDefault;
import static cofh.lib.util.constants.NBTTags.TAG_FILTER_TYPE;

public final class FilterHelper {

    private FilterHelper() {

    }

    public static boolean hasFilter(ItemStack stack) {

        return !getFilterType(stack).isEmpty();
    }

    public static String getFilterType(ItemStack stack) {

        return getPropertyWithDefault(stack, TAG_FILTER_TYPE, "");
    }

    public static boolean hasFilter(IFilterable filterable) {

        IFilter filter = filterable.getFilter();
        return filter != null && filter != EmptyFilter.INSTANCE;
    }

    public static boolean hasFilter(BlockEntity tile) {

        if (tile instanceof IFilterable filterable) {
            IFilter filter = filterable.getFilter();
            return filter != null && filter != EmptyFilter.INSTANCE;
        }
        return false;
    }

    public static boolean hasFilter(Entity entity) {

        if (entity instanceof IFilterable filterable) {
            IFilter filter = filterable.getFilter();
            return filter != null && filter != EmptyFilter.INSTANCE;
        }
        return false;
    }

    public static void openItemScreen(ServerPlayer player, MenuProvider containerSupplier) {

        NetworkHooks.openScreen(player, containerSupplier, buf -> {
            buf.writeVarInt(FilterHolderType.ITEM.ordinal());
            buf.writeVarInt(-1);
            buf.writeBlockPos(BlockPos.ZERO);
        });
    }

    public static void openTileScreen(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos) {

        NetworkHooks.openScreen(player, containerSupplier, buf -> {
            buf.writeVarInt(FilterHolderType.TILE.ordinal());
            buf.writeVarInt(-1);
            buf.writeBlockPos(pos);
        });
    }

    public static void openEntityScreen(ServerPlayer player, MenuProvider containerSupplier, int entityId) {

        NetworkHooks.openScreen(player, containerSupplier, buf -> {
            buf.writeVarInt(FilterHolderType.ENTITY.ordinal());
            buf.writeVarInt(entityId);
            buf.writeBlockPos(BlockPos.ZERO);
        });
    }

}

