package cofh.core.init;

import cofh.core.inventory.container.HeldFluidFilterContainer;
import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.core.inventory.container.TileFluidFilterContainer;
import cofh.core.inventory.container.TileItemFilterContainer;
import net.minecraftforge.common.extensions.IForgeMenuType;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.core.util.references.CoreIDs.*;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

        CONTAINERS.register(ID_CONTAINER_HELD_FLUID_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new HeldFluidFilterContainer(windowId, inv, getClientPlayer())));
        CONTAINERS.register(ID_CONTAINER_TILE_FLUID_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new TileFluidFilterContainer(windowId, getClientWorld(), data.readBlockPos(), inv, getClientPlayer())));

        CONTAINERS.register(ID_CONTAINER_HELD_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new HeldItemFilterContainer(windowId, inv, getClientPlayer())));
        CONTAINERS.register(ID_CONTAINER_TILE_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new TileItemFilterContainer(windowId, getClientWorld(), data.readBlockPos(), inv, getClientPlayer())));
    }

}
