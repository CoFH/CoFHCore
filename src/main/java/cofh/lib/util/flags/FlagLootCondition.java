package cofh.lib.util.flags;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;

/**
 * With thanks to Vazkii. :)
 */
public class FlagLootCondition implements ILootCondition {

    private final FlagManager manager;
    private final String flag;

    public FlagLootCondition(FlagManager manager, String flag) {

        this.manager = manager;
        this.flag = flag;
    }

    @Override
    public boolean test(LootContext lootContext) {

        return manager.getFlag(flag).getAsBoolean();
    }

    @Nonnull
    @Override
    public LootConditionType func_230419_b_() {

        return manager.flagConditionType;
    }

    // region SERIALIZER
    public static class Serializer implements ILootSerializer<FlagLootCondition> {

        private final FlagManager manager;

        public Serializer(FlagManager manager) {

            this.manager = manager;
        }

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull FlagLootCondition value, @Nonnull JsonSerializationContext context) {

            json.addProperty("flag", value.flag);
        }

        @Nonnull
        @Override
        public FlagLootCondition deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {

            return new FlagLootCondition(manager, json.getAsJsonPrimitive("flag").getAsString());
        }

    }
    // endregion
}