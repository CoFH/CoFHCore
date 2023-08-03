package cofh.lib.util.flags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class FlagManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Boolean>> FLAGS = new Object2ObjectOpenHashMap<>(64);

    public final ResourceLocation id;
    public LootItemConditionType flagConditionType;

    public FlagManager(String modId) {

        this(modId, "flag");
    }

    public FlagManager(String modId, String path) {

        id = new ResourceLocation(modId, path);
        CraftingHelper.register(new FlagRecipeCondition.Serializer(this, id));
        flagConditionType = new LootItemConditionType(new FlagLootCondition.Serializer(this));
    }

    public void setup() {

        Registry.register(Registry.LOOT_CONDITION_TYPE, id, flagConditionType);
    }

    private Supplier<Boolean> getOrCreateFlag(String flag) {

        synchronized (FLAGS) {
            FLAGS.putIfAbsent(flag, FALSE);
            return FLAGS.get(flag);
        }
    }

    public void setFlag(String flag, boolean enable) {

        synchronized (FLAGS) {
            FLAGS.put(flag, enable ? TRUE : FALSE);
        }
    }

    public void setFlag(String flag, Supplier<Boolean> condition) {

        synchronized (FLAGS) {
            FLAGS.put(flag, condition == null ? FALSE : condition);
        }
    }

    public Supplier<Boolean> getFlag(String flag) {

        return () -> getOrCreateFlag(flag).get();
    }

}
