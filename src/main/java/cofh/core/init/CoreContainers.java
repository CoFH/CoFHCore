package cofh.core.init;

import cofh.core.common.inventory.FluidFilterMenu;
import cofh.core.common.inventory.ItemFilterMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_FLUID_FILTER;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_ITEM_FILTER;

public class CoreContainers {

    private CoreContainers() {

    }

    public static void register() {

    }

    public static final RegistryObject<MenuType<FluidFilterMenu>> FLUID_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_FLUID_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new FluidFilterMenu(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));
    public static final RegistryObject<MenuType<ItemFilterMenu>> ITEM_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new ItemFilterMenu(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));

}
