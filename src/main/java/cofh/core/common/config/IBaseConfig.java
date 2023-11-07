package cofh.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IBaseConfig {

    void apply(ForgeConfigSpec.Builder builder);

    default void refresh() {

    }

}
