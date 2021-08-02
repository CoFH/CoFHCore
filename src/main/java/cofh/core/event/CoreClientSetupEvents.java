package cofh.core.event;

import cofh.lib.item.IColorableItem;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreClientSetupEvents {

    private static final List<Item> COLORABLE_ITEMS = new ArrayList<>();

    private CoreClientSetupEvents() {

    }

    @SubscribeEvent
    public static void colorSetupItem(final ColorHandlerEvent.Item event) {

        ItemColors colors = event.getItemColors();
        for (Item colorable : COLORABLE_ITEMS) {
            colors.register(((IColorableItem) colorable)::getColor, colorable);
        }
    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}
