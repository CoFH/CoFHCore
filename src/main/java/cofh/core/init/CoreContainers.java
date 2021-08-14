package cofh.core.init;

import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.core.inventory.container.TileItemFilterContainer;
import cofh.core.util.ProxyUtils;
import net.minecraftforge.common.extensions.IForgeContainerType;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.lib.util.references.CoreIDs.ID_CONTAINER_HELD_ITEM_FILTER;
import static cofh.lib.util.references.CoreIDs.ID_CONTAINER_TILE_ITEM_FILTER;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

        CONTAINERS.register(ID_CONTAINER_HELD_ITEM_FILTER, () -> IForgeContainerType.create((windowId, inv, data) -> new HeldItemFilterContainer(windowId, inv, ProxyUtils.getClientPlayer())));
        CONTAINERS.register(ID_CONTAINER_TILE_ITEM_FILTER, () -> IForgeContainerType.create((windowId, inv, data) -> new TileItemFilterContainer(windowId, ProxyUtils.getClientWorld(), data.readBlockPos(), inv, ProxyUtils.getClientPlayer())));
    }

}