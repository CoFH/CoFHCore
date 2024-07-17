package cofh.core.init;

import cofh.core.common.inventory.FluidFilterMenu;
import cofh.core.common.inventory.ItemFilterMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

import static cofh.core.CoFHCore.CONTAINERS;
import static cofh.core.util.ProxyUtils.getClientPlayer;
import static cofh.core.util.ProxyUtils.getClientWorld;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_FLUID_FILTER;
import static cofh.core.util.references.CoreIDs.ID_CONTAINER_ITEM_FILTER;

public class CoreMenus {

    private CoreMenus() {

    }

    public static void register() {

    }

    public static final DeferredHolder<MenuType<?>, MenuType<FluidFilterMenu>> FLUID_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_FLUID_FILTER, () -> IMenuTypeExtension.create((windowId, inv, data) -> new FluidFilterMenu(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));
    public static final DeferredHolder<MenuType<?>, MenuType<ItemFilterMenu>> ITEM_FILTER_CONTAINER = CONTAINERS.register(ID_CONTAINER_ITEM_FILTER, () -> IMenuTypeExtension.create((windowId, inv, data) -> new ItemFilterMenu(windowId, getClientWorld(), inv, getClientPlayer(), data.readVarInt(), data.readVarInt(), data.readBlockPos())));

}
