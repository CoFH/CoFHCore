package cofh.core.event;

import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber(modid = ID_COFH_CORE)
public class CoreCommonSetupEvents {

    protected static boolean tagsInitialized = false;

    private CoreCommonSetupEvents() {

    }

    public static boolean getTagsInitialized() {

        return tagsInitialized;
    }

    // Recipes reload during TagsUpdatedEvent or IdMapping on Server side.
    @SubscribeEvent
    public static void tagsUpdated(final TagsUpdatedEvent.CustomTagTypes event) {

        tagsInitialized = true;
    }

}
