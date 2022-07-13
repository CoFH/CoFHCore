package cofh.core.data;

import cofh.lib.loot.TileNBTSync;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (bus = Mod.EventBusSubscriber.Bus.MOD, modid = ID_COFH_CORE)
public class CoreDataGen {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {

        TileNBTSync.setup();

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();

        CoreTagsProvider.Block blockTags = new CoreTagsProvider.Block(gen, exFileHelper);

        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Item(gen, blockTags, exFileHelper));
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Fluid(gen, exFileHelper));

        gen.addProvider(event.includeServer(), new CoreLootTableProvider(gen));
        gen.addProvider(event.includeServer(), new CoreRecipeProvider(gen));

        gen.addProvider(event.includeClient(), new CoreBlockStateProvider(gen, exFileHelper));
        gen.addProvider(event.includeClient(), new CoreItemModelProvider(gen, exFileHelper));
    }

}
