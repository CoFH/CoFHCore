package cofh.core.data;

import cofh.lib.loot.TileNBTSync;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ID_COFH_CORE)
public class CoreDataGen {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {

        TileNBTSync.setup();

        if (event.includeServer()) {
            registerServerProviders(event);
        }
        if (event.includeClient()) {
            registerClientProviders(event);
        }
    }

    private static void registerServerProviders(GatherDataEvent event) {

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();

        CoreTagsProvider.Block blockTags = new CoreTagsProvider.Block(gen, exFileHelper);

        gen.addProvider(blockTags);
        gen.addProvider(new CoreTagsProvider.Item(gen, blockTags, exFileHelper));
        gen.addProvider(new CoreTagsProvider.Fluid(gen, exFileHelper));

        gen.addProvider(new CoreLootTableProvider(gen));
        gen.addProvider(new CoreRecipeProvider(gen));
    }

    private static void registerClientProviders(GatherDataEvent event) {

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();

        gen.addProvider(new CoreBlockStateProvider(gen, exFileHelper));
        gen.addProvider(new CoreItemModelProvider(gen, exFileHelper));
    }

}
