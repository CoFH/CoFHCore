package cofh.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IBaseConfig {

    void apply(ForgeConfigSpec.Builder builder);

    void refresh();

}
