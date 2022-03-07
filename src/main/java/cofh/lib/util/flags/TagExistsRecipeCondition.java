package cofh.lib.util.flags;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class TagExistsRecipeCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(ID_COFH_CORE, "tag_exists");
    private final TagKey<Item> tag_name;

    public TagExistsRecipeCondition(TagKey<Item> location) {

        this.tag_name = location;
    }

    @Override
    public ResourceLocation getID() {

        return NAME;
    }

    @Override
    public boolean test() {

        return Registry.ITEM.getTag(tag_name).filter(e -> e.size() != 0).isPresent();
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

            return new TagExistsRecipeCondition(ItemTags.create(new ResourceLocation(json.getAsJsonPrimitive("tag").getAsString())));
        }

        @Override
        public ResourceLocation getID() {

            return TagExistsRecipeCondition.NAME;
        }

    }
    // endregion
}
