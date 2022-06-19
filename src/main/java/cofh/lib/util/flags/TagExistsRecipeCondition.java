package cofh.lib.util.flags;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class TagExistsRecipeCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(ID_COFH_CORE, "tag_exists");
    private final TagKey<Item> tag;

    public TagExistsRecipeCondition(ResourceLocation tag) {

        this.tag = TagKey.create(Registry.ITEM_REGISTRY, tag);
    }

    @Override
    public ResourceLocation getID() {

        return NAME;
    }

    @Override
    public boolean test(IContext context) {

        return !context.getTag(tag).isEmpty();
    }

    @Override
    public String toString() {

        return "tag_exists(\"" + tag.location() + "\")";
    }

    // region SERIALIZER
    public static class Serializer implements IConditionSerializer<TagExistsRecipeCondition> {

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {

        }

        @Override
        public void write(JsonObject json, TagExistsRecipeCondition value) {

            json.addProperty("tag", value.tag.location().toString());
        }

        @Override
        public TagExistsRecipeCondition read(JsonObject json) {

            return new TagExistsRecipeCondition(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {

            return TagExistsRecipeCondition.NAME;
        }

    }
    // endregion
}
