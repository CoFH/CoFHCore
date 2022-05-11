package cofh.lib.config.world;

import cofh.lib.config.IBaseConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class OreConfig implements IBaseConfig {

    public static final OreConfig EMPTY_CONFIG = new OreConfig("invalid", 0, 0, 0, 0, List.of());

    protected String name;
    protected int count;
    protected int minY;
    protected int maxY;
    protected int size;
    // protected List<ResourceKey<Level>> dimensions;

    // private Set<ResourceKey<Level>> storedDimension = null;

    private ForgeConfigSpec.IntValue configCount;
    private ForgeConfigSpec.IntValue configMinY;
    private ForgeConfigSpec.IntValue configMaxY;
    private ForgeConfigSpec.IntValue configSize;
    // private ForgeConfigSpec.ConfigValue<List<? extends String>> configDimensions;

    public OreConfig(String name, int count, int minY, int maxY, int size, List<ResourceKey<Level>> dimensions) {

        super();
        this.name = name;
        this.count = count;
        this.minY = minY;
        this.maxY = maxY;
        this.size = size;
        // this.dimensions = dimensions;
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

    //    public Set<ResourceKey<Level>> getDimensions() {
    //
    //        if (storedDimension == null) {
    //            storedDimension = configDimensions.get().stream().map(o -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(o))).collect(Collectors.toSet());
    //        }
    //        return storedDimension;
    //    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push(name);

        configCount = builder.comment("Max number of veins per chunk; set to 0 to disable.").defineInRange("Vein Count", count, 0, 256);
        configSize = builder.comment("Max size of the vein.").defineInRange("Vein Size", size, 1, 256);
        configMinY = builder.comment("Minimum Y spawn").defineInRange("Min Y", minY, -64, 256);
        configMaxY = builder.comment("Maximum Y spawn").defineInRange("Max Y", maxY, -64, 256);
        // configDimensions = builder.comment("The dimensions that this ore should spawn in as a list (default [\"minecraft:overworld\"])").defineList("Valid Dimensions", dimensions.stream().map(ResourceKey::location).map(ResourceLocation::toString).collect(Collectors.toList()), (o) -> o instanceof String);

        builder.pop();
    }

    @Override
    public void refresh() {

        // storedDimension = null;
    }

}
