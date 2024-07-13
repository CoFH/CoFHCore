package cofh.core.init.data;

import cofh.core.init.data.providers.CoreBlockStateProvider;
import cofh.core.init.data.providers.CoreItemModelProvider;
import cofh.core.init.data.providers.CoreLootTableProvider;
import cofh.core.init.data.providers.CoreTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (bus = Mod.EventBusSubscriber.Bus.MOD, modid = ID_COFH_CORE)
public class CoreDataGen {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {

        // TileNBTSync.setup();

        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();

        CoreTagsProvider.Block blockTags = new CoreTagsProvider.Block(output, event.getLookupProvider(), exFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Item(output, event.getLookupProvider(), blockTags.contentsGetter(), exFileHelper));
        gen.addProvider(event.includeServer(), new CoreTagsProvider.Fluid(output, event.getLookupProvider(), exFileHelper));
        gen.addProvider(event.includeServer(), new CoreTagsProvider.DamageType(output, event.getLookupProvider(), exFileHelper));

        gen.addProvider(event.includeServer(), new CoreLootTableProvider(output));

        gen.addProvider(event.includeClient(), new CoreBlockStateProvider(output, exFileHelper));
        gen.addProvider(event.includeClient(), new CoreItemModelProvider(output, exFileHelper));
    }

}
