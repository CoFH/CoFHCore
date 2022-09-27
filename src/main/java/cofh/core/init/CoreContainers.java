package cofh.core.init;

import cofh.core.inventory.container.FluidFilterContainer;
import cofh.core.inventory.container.ItemFilterContainer;
import net.minecraftforge.common.extensions.IForgeMenuType;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_FLUID_FILTER;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_ITEM_FILTER;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

        CONTAINERS.register(ID_CONTAINER_FLUID_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new FluidFilterContainer(windowId, getClientWorld(), inv, getClientPlayer(), data.readBoolean(), data.readBlockPos(), data.readByte())));
        CONTAINERS.register(ID_CONTAINER_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new ItemFilterContainer(windowId, getClientWorld(), inv, getClientPlayer(), data.readBoolean(), data.readBlockPos(), data.readByte())));
    }

}
