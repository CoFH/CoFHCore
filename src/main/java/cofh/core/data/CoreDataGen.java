package cofh.core.data;

import cofh.core.data.providers.*;
import cofh.lib.loot.TileNBTSync;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
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
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();

        CoreTagsProvider.Block blockTags = new CoreTagsProvider.Block(output, event.getLookupProvider(), exFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Item(output, event.getLookupProvider(), blockTags.contentsGetter(), exFileHelper));
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Fluid(output, event.getLookupProvider(), exFileHelper));

        gen.addProvider(event.includeServer(), new CoreLootTableProvider(output));
        gen.addProvider(event.includeServer(), new CoreRecipeProvider(output));

        gen.addProvider(event.includeClient(), new CoreBlockStateProvider(output, exFileHelper));
        gen.addProvider(event.includeClient(), new CoreItemModelProvider(output, exFileHelper));
    }

}
