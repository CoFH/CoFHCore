package cofh.lib.config.world;

import cofh.lib.config.IBaseConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OreConfig implements IBaseConfig {

    protected String name;
    protected int count;
    protected int minY;
    protected int maxY;
    protected int size;
    protected List<ResourceKey<Level>> dimensions;

    private Set<ResourceKey<Level>> storedDimension = null;

    private ForgeConfigSpec.IntValue configCount;
    private ForgeConfigSpec.IntValue configMinY;
    private ForgeConfigSpec.IntValue configMaxY;
    private ForgeConfigSpec.IntValue configSize;
    private ForgeConfigSpec.ConfigValue<List<? extends String>> configDimensions;

    public OreConfig(String name, int count, int minY, int maxY, int size, List<ResourceKey<Level>> dimensions) {

        super();
        this.name = name;
        this.count = count;
        this.minY = minY;
        this.maxY = maxY;
        this.size = size;
        this.dimensions = dimensions;
    }

    public String getName() {

        return name;
    }

    public int getCount() {

        return configCount.get();
    }

    public int getMinY() {

        return configMinY.get();
    }

    public int getMaxY() {

        return configMaxY.get();
    }

    public int getSize() {

        return configSize.get();
    }

    public boolean shouldGenerate() {

        return getCount() > 0;
    }

    public Set<ResourceKey<Level>> getDimensions() {

        if (storedDimension == null) {
            storedDimension = configDimensions.get().stream().map(o -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
        }
        return storedDimension;
    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.comment(name + " ore generation.").push(name + "_oregen");

        configCount = builder.comment("Number of veins per chunk (set to 0 to disable).").defineInRange("oreChances", count, 0, 256);
        configSize = builder.comment("Max size of the vein.").defineInRange("veinSize", size, 1, 256);
        configMinY = builder.comment("Minimum Y spawn").defineInRange("minY", minY, -64, 256);
        configMaxY = builder.comment("Maximum Y spawn").defineInRange("maxY", maxY, -64, 256);
        configDimensions = builder.comment("The dimensions that this ore should spawn in as a list (default [\"minecraft:overworld\"])").defineList("dimensions", dimensions.stream().map(ResourceKey::location).map(ResourceLocation::toString).collect(Collectors.toList()), (o) -> o instanceof String);

        builder.pop();
    }

    @Override
    public void refresh() {

        storedDimension = null;
    }

}
