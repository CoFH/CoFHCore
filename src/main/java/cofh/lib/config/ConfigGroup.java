package cofh.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ConfigGroup implements IBaseConfig {

    List<IBaseConfig> subConfigs = new ArrayList<>();

    public ConfigGroup addSubconfig(IBaseConfig config) {

        subConfigs.add(config);
        return this;
    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        for (IBaseConfig cfg : subConfigs) {
            cfg.apply(builder);
        }
    }

    @Override
    public void refresh() {

        subConfigs.forEach(IBaseConfig::refresh);
    }

}
