package cofh.lib.util.filter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;

public interface IFilterableTile extends MenuProvider {

    IFilter getFilter();

    void onFilterChanged();

    boolean openGui(ServerPlayer player);

    boolean openFilterGui(ServerPlayer player);

    BlockPos pos();

    Level world();

}
