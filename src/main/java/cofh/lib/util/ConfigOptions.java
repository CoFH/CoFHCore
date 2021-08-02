package cofh.lib.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;

import java.util.Map;

public class ConfigOptions {

    private Map<String, ForgeConfigSpec.ConfigValue<?>> configs = new Object2ObjectOpenHashMap<>();

    public Boolean getBoolean(String key) {

        if (configs.containsKey(key)) {
            Object obj = configs.get(key);
            if (obj instanceof BooleanValue) {
                return ((BooleanValue) obj).get();
            }
        }
        return false;
    }

    public Integer getInt(String key) {

        if (configs.containsKey(key)) {
            Object obj = configs.get(key);
            if (obj instanceof IntValue) {
                return ((IntValue) obj).get();
            }
        }
        return 0;
    }

    public Long getLong(String key) {

        if (configs.containsKey(key)) {
            Object obj = configs.get(key);
            if (obj instanceof LongValue) {
                return ((LongValue) obj).get();
            }
        }
        return 0L;
    }

    public Double getDouble(String key) {

        if (configs.containsKey(key)) {
            Object obj = configs.get(key);
            if (obj instanceof DoubleValue) {
                return ((DoubleValue) obj).get();
            }
        }
        return 0D;
    }

}
