package cofh.core.util.filter;

import net.minecraft.server.level.ServerPlayer;

public interface IFilterable {

    IFilter getFilter();

    void onFilterChanged();

    boolean hasGui();

    boolean openGui(ServerPlayer player);

    boolean openFilterGui(ServerPlayer player);

}
