package cofh.lib.util.flags;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;

/**
 * With thanks to Vazkii. :)
 */
public class FlagLootCondition implements LootItemCondition {

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
    public LootItemConditionType getType() {

        return manager.flagConditionType;
    }

    // region SERIALIZER
    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FlagLootCondition> {

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
