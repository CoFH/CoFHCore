package cofh.core.util.filter;

import cofh.lib.api.block.entity.ITileLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface IFilterableTile extends ITileLocation, MenuProvider {

    IFilter getFilter(int filterId);

    void onFilterChanged(int filterId);

    boolean openGui(ServerPlayer player, int guiId);

    boolean openFilterGui(ServerPlayer player, int filterId);

}
