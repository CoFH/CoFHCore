package cofh.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ConfigGroup implements IBaseConfig {

    List<IBaseConfig> subConfigs = new ArrayList<>();

    public ConfigGroup addSubconfig(IBaseConfig config) {

        subConfigs.add(config);
        return this;
    }

    @Override
    public void apply(ModConfigSpec.Builder builder) {

        for (IBaseConfig cfg : subConfigs) {
            cfg.apply(builder);
        }
    }

}
