package cofh.core.init;

import cofh.core.common.inventory.FluidFilterContainer;
import cofh.core.common.inventory.ItemFilterContainer;
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

    public static final RegistryObject<MenuType<FluidFilterContainer>> FLUID_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_FLUID_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new FluidFilterContainer(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));
    public static final RegistryObject<MenuType<ItemFilterContainer>> ITEM_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_ITEM_FILTER, () -> IForgeMenuType.create((windowId, inv, data) -> new ItemFilterContainer(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));

}
