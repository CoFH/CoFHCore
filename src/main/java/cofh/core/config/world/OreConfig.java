package cofh.core.config.world;

import cofh.core.config.IBaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class OreConfig implements IBaseConfig {

    //public static final OreConfig EMPTY_CONFIG = new OreConfig("invalid", 0, 0, 0, 0, List.of(), () -> false);
    public static final OreConfig EMPTY_CONFIG = new OreConfig("invalid", () -> false);

    protected String name;
    //protected int count;
    //protected int minY;
    //protected int maxY;
    //protected int size;
    // protected List<ResourceKey<Level>> dimensions;
    protected Supplier<Boolean> enable = TRUE;
    protected Supplier<Boolean> generate = TRUE;

    // private Set<ResourceKey<Level>> storedDimension = null;

    //private Supplier<Integer> configCount;
    //private Supplier<Integer> configMinY;
    //private Supplier<Integer> configMaxY;
    //private Supplier<Integer> configSize;
    // private Supplier<List<? extends String>> configDimensions;

    public OreConfig(String name, Supplier<Boolean> enable) {

        this.name = name;
        //this.count = count;
        //this.minY = minY;
        //this.maxY = maxY;
        //this.size = size;
        // this.dimensions = dimensions;
        this.enable = enable;
    }

    public String getName() {

        return name;
    }

    //public int getCount() {
    //
    //    return !enable.get() ? 0 : configCount == null ? count : configCount.get();
    //}
    //
    //public int getMinY() {
    //
    //    return !enable.get() ? 0 : configMinY == null ? minY : configMinY.get();
    //}
    //
    //public int getMaxY() {
    //
    //    return !enable.get() ? 0 : configMaxY == null ? maxY : configMaxY.get();
    //}
    //
    //public int getSize() {
    //
    //    return !enable.get() ? 0 : configSize == null ? size : configSize.get();
    //}

    public boolean shouldGenerate() {

        return enable.get() && generate.get(); //getCount() > 0;
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

        if (enable.get()) {
            builder.push(name);

            generate = builder.comment("Whether this ore should spawn in the world.").define("Enable", true);
            //configCount = builder.comment("Max number of veins per chunk; set to 0 to disable.").defineInRange("Vein Count", count, 0, 64);
            //configSize = builder.comment("Max size of the vein.").defineInRange("Vein Size", size, 1, 64);
            //configMinY = builder.comment("Minimum Y spawn.").defineInRange("Min Y", minY, -2032, 2031);
            //configMaxY = builder.comment("Maximum Y spawn.").defineInRange("Max Y", maxY, -2032, 2031);
            // configDimensions = builder.comment("The dimensions that this ore should spawn in as a list (default [\"minecraft:overworld\"])").defineList("Valid Dimensions", dimensions.stream().map(ResourceKey::location).map(ResourceLocation::toString).collect(Collectors.toList()), (o) -> o instanceof String);

            builder.pop();
        }
    }

}
