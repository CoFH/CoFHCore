package cofh.lib.util.flags;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

/**
 * With thanks to Vazkii. :)
 */
public class FlagRecipeCondition implements ICondition {

    private final FlagManager manager;
    private final String flag;

    public FlagRecipeCondition(FlagManager manager, String flag) {

        this.manager = manager;
        this.flag = flag;
    }

    @Override
    public ResourceLocation getID() {

        return manager.id;
    }

    @Override
    public boolean test() {

        return manager.getFlag(flag).getAsBoolean();
    }

    // region SERIALIZER
    public static class Serializer implements IConditionSerializer<FlagRecipeCondition> {

        private final FlagManager manager;
        private final ResourceLocation location;

        public Serializer(FlagManager manager, ResourceLocation location) {

            this.manager = manager;
            this.location = location;
        }

        @Override
        public void write(JsonObject json, FlagRecipeCondition value) {

            json.addProperty("flag", value.flag);
        }

        @Override
        public FlagRecipeCondition read(JsonObject json) {

            return new FlagRecipeCondition(manager, json.getAsJsonPrimitive("flag").getAsString());
        }

        @Override
        public ResourceLocation getID() {

            return location;
        }

    }
    // endregion
}
