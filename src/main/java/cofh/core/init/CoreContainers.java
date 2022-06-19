package cofh.core.init;

import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.core.inventory.container.TileItemFilterContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.lib.util.references.CoreIDs.ID_CONTAINER_HELD_ITEM_FILTER;
import static cofh.lib.util.references.CoreIDs.ID_CONTAINER_TILE_ITEM_FILTER;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

        HELD_ITEM_FILTER = CONTAINERS.register(ID_CONTAINER_HELD_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new HeldItemFilterContainer(windowId, inv, getClientPlayer())));
        TILE_ITEM_FILTER = CONTAINERS.register(ID_CONTAINER_TILE_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new TileItemFilterContainer(windowId, getClientWorld(), data.readBlockPos(), inv, getClientPlayer())));
    }

    public static RegistryObject<MenuType<HeldItemFilterContainer>> HELD_ITEM_FILTER;
    public static RegistryObject<MenuType<TileItemFilterContainer>> TILE_ITEM_FILTER;

}
