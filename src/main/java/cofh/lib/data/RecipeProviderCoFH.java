package cofh.lib.data;

import cofh.lib.util.DeferredRegisterCoFH;
import cofh.lib.util.Utils;
import cofh.lib.util.flags.FlagManager;
import cofh.lib.util.flags.FlagRecipeCondition;
import cofh.lib.util.flags.TagExistsRecipeCondition;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static cofh.lib.util.constants.ModIds.ID_FORGE;

public class RecipeProviderCoFH extends VanillaRecipeProvider implements IConditionBuilder {

    protected final String modid;
    protected FlagManager manager;

    protected boolean advancements = false;

    public RecipeProviderCoFH(PackOutput output, String modid) {

        super(output);
        this.modid = modid;
    }

    public RecipeProviderCoFH generateAdvancements(boolean advancements) {

        this.advancements = advancements;
        return this;
    }

    @Override
    public void run(CachedOutput cache) {

        Set<ResourceLocation> set = Sets.newHashSet();
        buildCraftingRecipes((recipe) -> {
            if (!set.add(recipe.getId())) {
                throw new IllegalStateException("Duplicate recipe " + recipe.getId());
            } else {
                saveRecipe(cache, recipe.serializeRecipe(), this.recipePathProvider.json(recipe.getId()));
                if (advancements) {
                    JsonObject jsonobject = recipe.serializeAdvancement();
                    if (jsonobject != null) {
                        saveAdvancement(cache, jsonobject, this.advancementPathProvider.json(recipe.getAdvancementId()));
                    }
                }
            }
        });
    }

    @SafeVarargs
    protected final Ingredient fromTags(TagKey<Item>... tagsIn) {

        List<Ingredient> ingredients = new ArrayList<>(tagsIn.length);
        for (TagKey<Item> tag : tagsIn) {
            ingredients.add(Ingredient.of(tag));
        }
        return new CompoundIngredientWrapper(ingredients);
    }

    // region HELPERS
    protected void generateSmallPackingRecipe(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shaped(storage)
                .define('#', individual)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_at_least_4_" + individualName, hasItem(MinMaxBounds.Ints.atLeast(4), individual))
                .save(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generateSmallUnpackingRecipe(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapelessRecipeBuilder.shapeless(individual, 4)
                .requires(storage)
                .unlockedBy("has_at_least_4_" + individualName, hasItem(MinMaxBounds.Ints.atLeast(4), individual))
                .unlockedBy("has_" + storageName, has(storage))
                .save(consumer, this.modid + ":storage/" + individualName + suffix);
    }

    protected void generateSmallStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String packingSuffix, String unpackingSuffix) {

        generateSmallPackingRecipe(consumer, storage, individual, packingSuffix);
        generateSmallUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateSmallStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual) {

        generateSmallStorageRecipes(consumer, storage, individual, "", "_from_block");
    }

    protected void generatePackingRecipe(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shaped(storage)
                .define('#', individual)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_at_least_9_" + individualName, hasItem(MinMaxBounds.Ints.atLeast(9), individual))
                .save(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generatePackingRecipe(Consumer<FinishedRecipe> consumer, Item storage, Item individual, TagKey<Item> tag, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shaped(storage)
                .define('I', individual)
                .define('#', tag)
                .pattern("###")
                .pattern("#I#")
                .pattern("###")
                .unlockedBy("has_at_least_9_" + individualName, hasItem(MinMaxBounds.Ints.atLeast(9), individual))
                .save(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generateUnpackingRecipe(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapelessRecipeBuilder.shapeless(individual, 9)
                .requires(storage)
                .unlockedBy("has_at_least_9_" + individualName, hasItem(MinMaxBounds.Ints.atLeast(9), individual))
                .unlockedBy("has_" + storageName, has(storage))
                .save(consumer, this.modid + ":storage/" + individualName + suffix);
    }

    protected void generateStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual, TagKey<Item> tag, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, tag, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual, TagKey<Item> tag) {

        generateStorageRecipes(consumer, storage, individual, tag, "", "_from_block");
    }

    protected void generateStorageRecipes(Consumer<FinishedRecipe> consumer, Item storage, Item individual) {

        generateStorageRecipes(consumer, storage, individual, "", "_from_block");
    }

    protected void generateTypeRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, String type) {

        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);
        Item block = reg.get(type + "_block");
        Item nugget = reg.get(type + "_nugget");

        Item raw = reg.get("raw_" + type);
        Item rawBlock = reg.get("raw_" + type + "_block");

        TagKey<Item> ingotTag = forgeTag("ingots/" + type);
        TagKey<Item> gemTag = forgeTag("gems/" + type);
        TagKey<Item> nuggetTag = forgeTag("nuggets/" + type);
        TagKey<Item> rawTag = forgeTag("raw_materials/" + type);

        if (block != null) {
            if (ingot != null) {
                generateStorageRecipes(consumer, block, ingot, ingotTag, "", "_from_block");
            } else if (gem != null) {
                generateStorageRecipes(consumer, block, gem, gemTag, "", "_from_block");
            }
        }
        if (nugget != null) {
            if (ingot != null) {
                generateStorageRecipes(consumer, ingot, nugget, nuggetTag, "_from_nuggets", "_from_ingot");
            } else if (gem != null) {
                generateStorageRecipes(consumer, gem, nugget, nuggetTag, "_from_nuggets", "_from_gem");
            }
        }
        if (rawBlock != null) {
            if (raw != null) {
                generateStorageRecipes(consumer, rawBlock, raw, rawTag, "", "_from_block");
            }
        }
        generateGearRecipe(reg, consumer, type);
    }

    protected void generateGearRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, String type) {

        Item gear = reg.get(type + "_gear");
        if (gear == null) {
            return;
        }
        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);

        TagKey<Item> ingotTag = forgeTag("ingots/" + type);
        TagKey<Item> gemTag = forgeTag("gems/" + type);

        if (ingot != null) {
            ShapedRecipeBuilder.shaped(gear)
                    .define('#', ingotTag)
                    .define('i', Tags.Items.NUGGETS_IRON)
                    .pattern(" # ")
                    .pattern("#i#")
                    .pattern(" # ")
                    .unlockedBy("has_" + name(ingot), has(ingotTag))
                    .save(consumer, this.modid + ":parts/" + name(gear));
        }
        if (gem != null) {
            ShapedRecipeBuilder.shaped(gear)
                    .define('#', gemTag)
                    .define('i', Tags.Items.NUGGETS_IRON)
                    .pattern(" # ")
                    .pattern("#i#")
                    .pattern(" # ")
                    .unlockedBy("has_" + name(gem), has(gemTag))
                    .save(consumer, this.modid + ":parts/" + name(gear));
        }
    }

    protected void generateGearRecipe(Consumer<FinishedRecipe> consumer, Item gear, Item material, TagKey<Item> tag) {

        if (gear == null || material == null || tag == null) {
            return;
        }
        ShapedRecipeBuilder.shaped(gear)
                .define('#', tag)
                .define('i', Tags.Items.NUGGETS_IRON)
                .pattern(" # ")
                .pattern("#i#")
                .pattern(" # ")
                .unlockedBy("has_" + name(material), has(tag))
                .save(consumer, this.modid + ":parts/" + name(gear));
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp) {

        generateSmeltingRecipe(reg, consumer, input, output, xp, "", "");
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingRecipe(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), output, xp, 200)
                .unlockedBy("has_" + name(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, String material, float xp) {

        generateSmeltingAndBlastingRecipes(reg, consumer, material, xp, "smelting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, String material, float xp, String folder) {

        Item ore = reg.get(material + "_ore");
        Item deep = reg.get("deepslate_" + material + "_ore");

        Item raw = reg.get("raw_" + material);
        Item ingot = reg.get(material + "_ingot");
        Item gem = reg.get(material);
        Item dust = reg.get(material + "_dust");

        if (ingot != null) {
            if (dust != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, dust, ingot, 0.0F, folder, "_dust");
            }
            if (raw != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, raw, ingot, 0.7F, folder, "_raw");
            }
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, ingot, xp, folder, "_ore");
            }
            if (deep != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, deep, ingot, xp, folder, "_deepslate_ore");
            }
        } else if (gem != null) {
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, gem, xp, folder, "_ore");
            }
            if (deep != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, deep, gem, xp, folder, "_deepslate_ore");
            }
        }
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingAndBlastingRecipes(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), output, xp, 200)
                .unlockedBy(getHasName(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), output, xp, 100)
                .unlockedBy(getHasName(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_blasting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, TagKey<Item> input, String condition, Item output, float xp, String folder) {

        generateSmeltingAndBlastingRecipes(reg, consumer, input, condition, output, xp, folder, "");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, TagKey<Item> input, String condition, Item output, float xp, String folder, String suffix) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), output, xp, 200)
                .unlockedBy(condition, has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), output, xp, 100)
                .unlockedBy(condition, has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_blasting");
    }

    protected void generateSmeltingAndCookingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingAndCookingRecipes(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingAndCookingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), output, xp, 200)
                .unlockedBy(getHasName(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), output, xp, 100)
                .unlockedBy(getHasName(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smoking");

        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), output, xp, 600)
                .unlockedBy(getHasName(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_campfire_cooking");
    }

    protected void generateSmeltingAndCookingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, TagKey<Item> input, String condition, Item output, float xp, String folder) {

        generateSmeltingAndCookingRecipes(reg, consumer, input, condition, output, xp, folder, "");
    }

    protected void generateSmeltingAndCookingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, TagKey<Item> input, String condition, Item output, float xp, String folder, String suffix) {

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), output, xp, 200)
                .unlockedBy(condition, has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), output, xp, 100)
                .unlockedBy(condition, has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smoking");

        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), output, xp, 600)
                .unlockedBy(condition, has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_campfire_cooking");
    }

    protected void generateStonecuttingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, String folder) {

        generateStonecuttingRecipe(reg, consumer, input, output, folder, "");
    }

    protected void generateStonecuttingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<FinishedRecipe> consumer, Item input, Item output, String folder, String suffix) {

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), output)
                .unlockedBy("has_" + name(input), has(input))
                .save(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_stonecutting");
    }

    // TODO: Change if Mojang implements some better defaults...
    public InventoryChangeTrigger.TriggerInstance hasItem(MinMaxBounds.Ints amount, ItemLike itemIn) {

        return inventoryTrigger(new ItemPredicate(null, Set.of(itemIn.asItem()), amount, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY)); // ItemPredicate.Builder.create().item(itemIn).count(amount).build());
    }

    protected static TagKey<Item> forgeTag(String name) {

        return ItemTags.create(new ResourceLocation(ID_FORGE, name));
    }

    protected static String name(Block block) {

        return Utils.getName(block);
    }

    protected static String name(Item item) {

        return Utils.getName(item);
    }
    // endregion

    protected static class CompoundIngredientWrapper extends CompoundIngredient {

        public CompoundIngredientWrapper(List<Ingredient> children) {

            super(children);
        }

    }

    // region CONDITIONAL RECIPES
    protected static class ConditionalRecipeWrapper implements FinishedRecipe {

        protected FinishedRecipe recipe;
        protected List<ICondition> conditions = new ArrayList<>();

        public ConditionalRecipeWrapper(FinishedRecipe recipe) {

            this.recipe = recipe;
        }

        public ConditionalRecipeWrapper addCondition(ICondition condition) {

            this.conditions.add(condition);
            return this;
        }

        public ConditionalRecipeWrapper addConditions(List<ICondition> conditions) {

            this.conditions.addAll(conditions);
            return this;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {

            recipe.serializeRecipeData(json);
        }

        @Override
        public JsonObject serializeRecipe() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(this.getType()).toString());
            this.serializeRecipeData(jsonobject);
            if (!conditions.isEmpty()) {
                JsonArray conditionArray = new JsonArray();
                for (ICondition condition : conditions) {
                    conditionArray.add(CraftingHelper.serialize(condition));
                }
                jsonobject.add("conditions", conditionArray);
            }
            return jsonobject;
        }

        @Override
        public ResourceLocation getId() {

            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {

            return recipe.getType();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {

            return recipe.serializeAdvancement();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {

            return recipe.getAdvancementId();
        }

    }

    protected ConditionalRecipeConsumer withConditions(Consumer<FinishedRecipe> consumer) {

        return new ConditionalRecipeConsumer(consumer);
    }

    protected class ConditionalRecipeConsumer implements Consumer<FinishedRecipe> {

        protected final Consumer<FinishedRecipe> consumer;
        protected List<ICondition> conditions = new ArrayList<>();

        public ConditionalRecipeConsumer(Consumer<FinishedRecipe> consumer) {

            this.consumer = consumer;
        }

        public ConditionalRecipeConsumer addCondition(ICondition condition) {

            this.conditions.add(condition);
            return this;
        }

        public ConditionalRecipeConsumer addConditions(List<ICondition> conditions) {

            this.conditions.addAll(conditions);
            return this;
        }

        public ConditionalRecipeConsumer tagExists(TagKey<Item> tag) {

            this.conditions.add(new TagExistsRecipeCondition(tag.location()));
            return this;
        }

        public ConditionalRecipeConsumer flag(String flag) {

            if (manager != null) {
                this.conditions.add(new FlagRecipeCondition(manager, flag));
            }
            return this;
        }

        @Override
        public void accept(FinishedRecipe recipe) {

            if (!conditions.isEmpty()) {
                consumer.accept(new ConditionalRecipeWrapper(recipe).addConditions(conditions));
            } else {
                consumer.accept(recipe);
            }
        }

    }
    // endregion
}
