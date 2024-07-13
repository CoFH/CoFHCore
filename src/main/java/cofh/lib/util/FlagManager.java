package cofh.lib.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class FlagManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Boolean>> FLAGS = new Object2ObjectOpenHashMap<>(64);

    private FlagManager() {

    }

    private static Supplier<Boolean> getOrCreateFlag(String flag) {

        synchronized (FLAGS) {
            FLAGS.putIfAbsent(flag, FALSE);
            return FLAGS.get(flag);
        }
    }

    public static void setFlag(String flag, boolean enable) {

        synchronized (FLAGS) {
            FLAGS.put(flag, enable ? TRUE : FALSE);
        }
    }

    public static void setFlag(String flag, Supplier<Boolean> condition) {

        synchronized (FLAGS) {
            FLAGS.put(flag, condition == null ? FALSE : condition);
        }
    }

    public static Supplier<Boolean> getFlag(String flag) {

        return () -> getOrCreateFlag(flag).get();
    }

}
