package cofh.lib.util.flags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.FALSE;
import static cofh.lib.util.constants.Constants.TRUE;

public class FlagManager {

    private static final Object2ObjectOpenHashMap<String, BooleanSupplier> FLAGS = new Object2ObjectOpenHashMap<>(64);

    public final ResourceLocation id;
    public LootItemConditionType flagConditionType;

    public FlagManager(String modId) {

        this(modId, "flag");
    }

    public FlagManager(String modId, String path) {

        id = new ResourceLocation(modId, path);
        CraftingHelper.register(new FlagRecipeCondition.Serializer(this, id));
        flagConditionType = new LootItemConditionType(new FlagLootCondition.Serializer(this));
        // TODO Lemming, Vanilla Registries now get Frozen, we likely need to manually un-freeze this to register things.
//        Registry.register(Registry.LOOT_CONDITION_TYPE, id, flagConditionType);
    }

    private BooleanSupplier getOrCreateFlag(String flag) {

        FLAGS.putIfAbsent(flag, FALSE);
        return FLAGS.get(flag);
    }

    public synchronized void setFlag(String flag, boolean enable) {

        FLAGS.put(flag, enable ? TRUE : FALSE);
    }

    public synchronized void setFlag(String flag, BooleanSupplier condition) {

        FLAGS.put(flag, condition == null ? FALSE : condition);
    }

    public BooleanSupplier getFlag(String flag) {

        return () -> getOrCreateFlag(flag).getAsBoolean();
    }

}
