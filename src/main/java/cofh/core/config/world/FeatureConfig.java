package cofh.core.config.world;

import cofh.core.config.IBaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class FeatureConfig implements IBaseConfig {

    public static final FeatureConfig EMPTY_CONFIG = new FeatureConfig("empty", FALSE);

    protected String name;
    protected Supplier<Boolean> enable = FALSE;
    protected Supplier<Boolean> generate = TRUE;

    public FeatureConfig(String name, Supplier<Boolean> enable) {

        this.name = name;
        this.enable = enable;
    }

    public String getName() {

        return name;
    }

    public boolean shouldGenerate() {

        return enable.get() && generate.get();
    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        if (enable.get()) {
            builder.push(name);

            generate = builder.comment("Whether this ore should spawn in the world.").define("Enable", true);

            builder.pop();
        }
    }

}
