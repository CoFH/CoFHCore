package cofh.lib.data;

import cofh.lib.util.DeferredRegisterCoFH;
import cofh.lib.util.flags.FlagManager;
import cofh.lib.util.flags.FlagRecipeCondition;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static cofh.lib.util.constants.Constants.ID_FORGE;

public class RecipeProviderCoFH extends RecipeProvider implements IConditionBuilder {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final String modid;
    protected FlagManager manager;

    public RecipeProviderCoFH(DataGenerator generatorIn, String modid) {

        super(generatorIn);
        this.modid = modid;
    }

    @Override
    public void act(DirectoryCache cache) {

        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        registerRecipes((recipe) -> {
            if (!set.add(recipe.getID())) {
                LOGGER.error("Duplicate recipe " + recipe.getID());
            } else {
                saveRecipe(cache, recipe.getRecipeJson(), path.resolve("data/" + recipe.getID().getNamespace() + "/recipes/" + recipe.getID().getPath() + ".json"));
            }
            // We do not generate advancements - they add a LOT of time to server connection.

            // JsonObject jsonobject = recipe.getAdvancementJson();
            // if (jsonobject != null) {
            //     saveRecipeAdvancement(cache, jsonobject, path.resolve("data/" + recipe.getID().getNamespace() + "/advancements/" + recipe.getAdvancementID().getPath() + ".json"));
            // }
        });
    }

    @SafeVarargs
    protected final Ingredient fromTags(ITag.INamedTag<Item>... tagsIn) {

        List<Ingredient> ingredients = new ArrayList<>(tagsIn.length);
        for (ITag.INamedTag<Item> tag : tagsIn) {
            ingredients.add(Ingredient.fromTag(tag));
        }
        return new CompoundIngredientWrapper(ingredients);
    }

    // region HELPERS
    protected void generatePackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shapedRecipe(storage)
                .key('#', individual)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .build(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generatePackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shapedRecipe(storage)
                .key('I', individual)
                .key('#', tag)
                .patternLine("###")
                .patternLine("#I#")
                .patternLine("###")
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .build(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generateUnpackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapelessRecipeBuilder.shapelessRecipe(individual, 9)
                .addIngredient(storage)
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .addCriterion("has_" + storageName, hasItem(storage))
                .build(consumer, this.modid + ":storage/" + individualName + suffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, tag, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag) {

        generateStorageRecipes(consumer, storage, individual, tag, "", "_from_block");
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual) {

        generateStorageRecipes(consumer, storage, individual, "", "_from_block");
    }

    protected void generateTypeRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String type) {

        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);
        Item block = reg.get(type + "_block");
        Item nugget = reg.get(type + "_nugget");

        ITag.INamedTag<Item> ingotTag = forgeTag("ingots/" + type);
        ITag.INamedTag<Item> gemTag = forgeTag("gems/" + type);
        ITag.INamedTag<Item> nuggetTag = forgeTag("nuggets/" + type);

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
        generateGearRecipe(reg, consumer, type);
    }

    protected void generateGearRecipe(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String type) {

        Item gear = reg.get(type + "_gear");
        if (gear == null) {
            return;
        }
        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);

        ITag.INamedTag<Item> ingotTag = forgeTag("ingots/" + type);
        ITag.INamedTag<Item> gemTag = forgeTag("gems/" + type);

        if (ingot != null) {
            ShapedRecipeBuilder.shapedRecipe(gear)
                    .key('#', ingotTag)
                    .key('i', Tags.Items.NUGGETS_IRON)
                    .patternLine(" # ")
                    .patternLine("#i#")
                    .patternLine(" # ")
                    .addCriterion("has_" + name(ingot), hasItem(ingotTag))
                    .build(consumer, this.modid + ":parts/" + name(gear));
        }
        if (gem != null) {
            ShapedRecipeBuilder.shapedRecipe(gear)
                    .key('#', gemTag)
                    .key('i', Tags.Items.NUGGETS_IRON)
                    .patternLine(" # ")
                    .patternLine("#i#")
                    .patternLine(" # ")
                    .addCriterion("has_" + name(gem), hasItem(gemTag))
                    .build(consumer, this.modid + ":parts/" + name(gear));
        }
    }

    protected void generateGearRecipe(Consumer<IFinishedRecipe> consumer, Item gear, Item material, ITag.INamedTag<Item> tag) {

        if (gear == null || material == null || tag == null) {
            return;
        }
        ShapedRecipeBuilder.shapedRecipe(gear)
                .key('#', tag)
                .key('i', Tags.Items.NUGGETS_IRON)
                .patternLine(" # ")
                .patternLine("#i#")
                .patternLine(" # ")
                .addCriterion("has_" + name(material), hasItem(tag))
                .build(consumer, this.modid + ":parts/" + name(gear));
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp) {

        generateSmeltingRecipe(reg, consumer, input, output, xp, "", "");
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingRecipe(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingRecipe(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, 200)
                .addCriterion("has_" + name(input), hasItem(input))
                .build(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String material, float xp) {

        generateSmeltingAndBlastingRecipes(reg, consumer, material, xp, "smelting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String material, float xp, String folder) {

        Item ore = reg.get(material + "_ore");
        Item ingot = reg.get(material + "_ingot");
        Item gem = reg.get(material);
        Item dust = reg.get(material + "_dust");

        if (ingot != null) {
            if (dust != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, dust, ingot, 0, folder, "_dust");
            }
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, ingot, xp, folder, "_ore");
            }
        } else if (gem != null) {
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, gem, xp, folder, "_ore");
            }
        }
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingAndBlastingRecipes(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, 200)
                .addCriterion("has_" + name(input), hasItem(input))
                .build(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, 100)
                .addCriterion("has_" + name(input), hasItem(input))
                .build(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_blasting");
    }

    // TODO: Change if Mojang implements some better defaults...
    public InventoryChangeTrigger.Instance hasItem(MinMaxBounds.IntBound amount, IItemProvider itemIn) {

        return hasItem(new ItemPredicate(null, itemIn.asItem(), amount, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.enchantments, EnchantmentPredicate.enchantments, null, NBTPredicate.ANY)); // ItemPredicate.Builder.create().item(itemIn).count(amount).build());
    }

    protected static Tags.IOptionalNamedTag<Item> forgeTag(String name) {

        return ItemTags.createOptional(new ResourceLocation(ID_FORGE, name));
    }

    protected static String name(Block block) {

        return block.getRegistryName() == null ? "" : block.getRegistryName().getPath();
    }

    protected static String name(Item item) {

        return item.getRegistryName() == null ? "" : item.getRegistryName().getPath();
    }
    // endregion

    protected static class CompoundIngredientWrapper extends CompoundIngredient {

        public CompoundIngredientWrapper(List<Ingredient> children) {

            super(children);
        }

    }

    // region CONDITIONAL RECIPES
    protected static class ConditionalRecipeWrapper implements IFinishedRecipe {

        protected IFinishedRecipe recipe;
        protected List<ICondition> conditions = new ArrayList<>();

        public ConditionalRecipeWrapper(IFinishedRecipe recipe) {

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
        public void serialize(JsonObject json) {

            recipe.serialize(json);
        }

        @Override
        public JsonObject getRecipeJson() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.getSerializer()).toString());
            this.serialize(jsonobject);
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
        public ResourceLocation getID() {

            return recipe.getID();
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {

            return recipe.getSerializer();
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {

            return recipe.getAdvancementJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {

            return recipe.getAdvancementID();
        }

    }

    protected ConditionalRecipeConsumer withConditions(Consumer<IFinishedRecipe> consumer) {

        return new ConditionalRecipeConsumer(consumer);
    }

    protected class ConditionalRecipeConsumer implements Consumer<IFinishedRecipe> {

        protected final Consumer<IFinishedRecipe> consumer;
        protected List<ICondition> conditions = new ArrayList<>();

        public ConditionalRecipeConsumer(Consumer<IFinishedRecipe> consumer) {

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

        public ConditionalRecipeConsumer flag(String flag) {

            if (manager != null) {
                this.conditions.add(new FlagRecipeCondition(manager, flag));
            }
            return this;
        }

        @Override
        public void accept(IFinishedRecipe recipe) {

            if (!conditions.isEmpty()) {
                consumer.accept(new ConditionalRecipeWrapper(recipe).addConditions(conditions));
            } else {
                consumer.accept(recipe);
            }
        }

    }
    // endregion
}
