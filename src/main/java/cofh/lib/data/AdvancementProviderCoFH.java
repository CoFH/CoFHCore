package cofh.lib.data;

import cofh.lib.util.flags.FlagManager;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AdvancementProviderCoFH extends AdvancementProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    protected final DataGenerator generator;
    protected FlagManager manager;

    protected List<Consumer<Consumer<Advancement>>> advancements = new ArrayList<>();

    public AdvancementProviderCoFH(DataGenerator generatorIn) {

        super(generatorIn);
        this.generator = generatorIn;
    }

    @Override
    public void run(DirectoryCache cache) {

        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                LOGGER.error("Duplicate advancement " + advancement.getId());
            } else {
                Path path1 = getPath(path, advancement);
                try {
                    IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }
            }
        };
        for (Consumer<Consumer<Advancement>> advConsumer : this.advancements) {
            advConsumer.accept(consumer);
        }
    }

    protected static Path getPath(Path pathIn, Advancement advancementIn) {

        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

}
