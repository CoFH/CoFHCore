package cofh.lib.util.flags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class FlagManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Boolean>> FLAGS = new Object2ObjectOpenHashMap<>(64);

    public static LootItemConditionType FLAG_SET;

    private FlagManager() {

    }

    public static void setup() {

        FLAG_SET = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("cofh:flag_set"), new LootItemConditionType(FlagSetLootCondition.CODEC));
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
