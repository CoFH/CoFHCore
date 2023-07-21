package cofh.core.util.filter;

import cofh.core.util.helpers.FilterHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public interface IFilterableItem {

    IFilter getFilter(ItemStack stack);

    void onFilterChanged(ItemStack stack);

    default boolean hasGui(ItemStack stack) {

        return this instanceof MenuProvider;
    }

    default boolean openGui(ServerPlayer player, ItemStack stack) {

        if (hasGui(stack)) {
            NetworkHooks.openScreen(player, (MenuProvider) this);
        }
        return true;
    }

    default boolean openFilterGui(ServerPlayer player, ItemStack stack) {

        if (FilterHelper.hasFilter(stack) && getFilter(stack) instanceof MenuProvider filter) {
            FilterHelper.openItemScreen(player, filter);
            return true;
        }
        return false;
    }

}
