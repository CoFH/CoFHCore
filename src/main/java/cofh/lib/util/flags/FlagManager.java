package cofh.lib.util.flags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.loot.LootConditionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.FALSE;
import static cofh.lib.util.constants.Constants.TRUE;

public class FlagManager {

    private static final Object2ObjectOpenHashMap<String, BooleanSupplier> FLAGS = new Object2ObjectOpenHashMap<>(64);

    public final ResourceLocation id;
    public LootConditionType flagConditionType;

    public FlagManager(String modId) {

        this(modId, "flag");
    }

    public FlagManager(String modId, String path) {

        id = new ResourceLocation(modId, path);
        CraftingHelper.register(new FlagRecipeCondition.Serializer(this, id));
        flagConditionType = new LootConditionType(new FlagLootCondition.Serializer(this));
        Registry.register(Registry.LOOT_CONDITION_TYPE, id, flagConditionType);
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
