package cofh.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface IBaseConfig {

    void apply(ModConfigSpec.Builder builder);

    default void refresh() {

    }

}
