package cofh.lib.util.flags;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class TagExistsRecipeCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(ID_COFH_CORE, "tag_exists");
    private final ResourceLocation tag_name;

    public TagExistsRecipeCondition(ResourceLocation location) {

        this.tag_name = location;
    }

    @Override
    public ResourceLocation getID() {

        return NAME;
    }

    @Override
    public boolean test() {

        ITag<Item> tag = TagCollectionManager.getInstance().getItems().getTag(tag_name);
        return tag != null && !tag.getValues().isEmpty();
    }

    @Override
    public String toString() {

        return "tag_exists(\"" + tag_name + "\")";
    }

    // region SERIALIZER
    public static class Serializer implements IConditionSerializer<TagExistsRecipeCondition> {

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {

        }

        @Override
        public void write(JsonObject json, TagExistsRecipeCondition value) {

            json.addProperty("tag", value.tag_name.toString());
        }

        @Override
        public TagExistsRecipeCondition read(JsonObject json) {

            return new TagExistsRecipeCondition(new ResourceLocation(json.getAsJsonPrimitive("tag").getAsString()));
        }

        @Override
        public ResourceLocation getID() {

            return TagExistsRecipeCondition.NAME;
        }

    }
    // endregion
}
