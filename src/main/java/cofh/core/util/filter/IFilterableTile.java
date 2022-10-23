package cofh.core.util.filter;

import cofh.lib.api.block.entity.ITileLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IFilterableTile extends ITileLocation {

    IFilter getFilter();

    void onFilterChanged();

    boolean openGui(ServerPlayer player);

    boolean openFilterGui(ServerPlayer player);

}
