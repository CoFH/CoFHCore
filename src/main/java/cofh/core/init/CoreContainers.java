package cofh.core.init;

import cofh.core.content.inventory.container.HeldItemFilterContainer;
import cofh.core.content.inventory.container.TileItemFilterContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_HELD_ITEM_FILTER;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_TILE_ITEM_FILTER;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

    }

    public static final RegistryObject<MenuType<HeldItemFilterContainer>> HELD_ITEM_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_HELD_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new HeldItemFilterContainer(windowId, inv, getClientPlayer())));
    public static final RegistryObject<MenuType<TileItemFilterContainer>> TILE_ITEM_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_TILE_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new TileItemFilterContainer(windowId, getClientWorld(), data.readBlockPos(), inv, getClientPlayer())));

}
