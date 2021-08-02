package cofh.lib.util.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static cofh.lib.util.constants.Constants.BASE_CHANCE_LOCKED;
import static cofh.lib.util.constants.Constants.BUCKET_VOLUME;

public abstract class RecipeJsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private RecipeJsonUtils() {

    }

    // region HELPERS
    public static Ingredient parseIngredient(JsonElement element) {

        if (element == null || element.isJsonNull()) {
            return Ingredient.fromStacks(ItemStack.EMPTY);
        }
        Ingredient ingredient;

        if (element.isJsonArray()) {
            try {
                ingredient = Ingredient.deserialize(element);
            } catch (Throwable t) {
                ingredient = Ingredient.fromStacks(ItemStack.EMPTY);
            }
        } else {
            JsonElement subElement = element.getAsJsonObject();
            try {
                JsonObject object = subElement.getAsJsonObject();
                if (object.has(VALUE)) {
                    ingredient = Ingredient.deserialize(object.get(VALUE));
                } else {
                    ingredient = Ingredient.deserialize(subElement);
                }
                int count = 1;
                if (object.has(COUNT)) {
                    count = object.get(COUNT).getAsInt();
                } else if (object.has(AMOUNT)) {
                    count = object.get(AMOUNT).getAsInt();
                }
                if (count > 1) {
                    for (ItemStack stack : ingredient.getMatchingStacks()) {
                        stack.setCount(count);
                    }
                }
            } catch (Throwable t) {
                ingredient = Ingredient.fromStacks(ItemStack.EMPTY);
            }
        }
        return ingredient;
    }

    public static void parseInputs(List<Ingredient> ingredients, List<FluidStack> fluids, JsonElement element) {

        if (element.isJsonArray()) {
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                if (arrayElement.isJsonArray()) {
                    ingredients.add(parseIngredient(arrayElement.getAsJsonArray()));
                } else if (arrayElement.isJsonObject()) {
                    if (arrayElement.getAsJsonObject().has(FLUID)) {
                        fluids.add(parseFluidStack(arrayElement));
                    } else {
                        ingredients.add(parseIngredient(arrayElement.getAsJsonObject()));
                    }
                }
            }
        } else if (element.getAsJsonObject().has(FLUID)) {
            fluids.add(parseFluidStack(element));
        } else {
            ingredients.add(parseIngredient(element.getAsJsonObject()));
        }
    }

    public static void parseOutputs(List<ItemStack> items, List<Float> chances, List<FluidStack> fluids, JsonElement element) {

        if (element == null) {
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                if (arrayElement.getAsJsonObject().has(FLUID)) {
                    fluids.add(parseFluidStack(arrayElement));
                } else {
                    ItemStack stack = parseItemStack(arrayElement);
                    if (!stack.isEmpty()) {
                        items.add(stack);
                        chances.add(parseItemChance(arrayElement));
                    }
                }
            }
        } else if (element.getAsJsonObject().has(FLUID)) {
            fluids.add(parseFluidStack(element));
        } else {
            ItemStack stack = parseItemStack(element);
            if (!stack.isEmpty()) {
                items.add(stack);
                chances.add(parseItemChance(element));
            }
        }
    }

    public static ItemStack parseItemStack(JsonElement element) {

        if (element == null || element.isJsonNull()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack;
        Item item = null;
        int count = 1;

        if (element.isJsonPrimitive()) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(element.getAsString()));
            return item == null ? ItemStack.EMPTY : new ItemStack(item);
        } else {
            JsonObject itemObject = element.getAsJsonObject();

            /* COUNT */
            if (itemObject.has(COUNT)) {
                count = itemObject.get(COUNT).getAsInt();
            } else if (itemObject.has(AMOUNT)) {
                count = itemObject.get(AMOUNT).getAsInt();
            }

            /* ITEM */
            if (itemObject.has(ITEM)) {
                item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemObject.get(ITEM).getAsString()));
            }
            if (item == null) {
                return ItemStack.EMPTY;
            }
            stack = new ItemStack(item, count);

            /* NBT */
            if (itemObject.has(NBT)) {
                JsonElement nbtElement = itemObject.get(NBT);
                CompoundNBT nbt;
                try {
                    if (nbtElement.isJsonObject()) {
                        nbt = JsonToNBT.getTagFromJson(GSON.toJson(nbtElement));
                    } else {
                        nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(nbtElement, NBT));
                    }
                    stack.setTag(nbt);
                } catch (Exception e) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    public static FluidStack parseFluidStack(JsonElement element) {

        if (element == null || element.isJsonNull()) {
            return FluidStack.EMPTY;
        }
        FluidStack stack;
        Fluid fluid = null;
        int amount = BUCKET_VOLUME;

        if (element.isJsonPrimitive()) {
            fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(element.getAsString()));
            return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
        } else {
            JsonObject fluidObject = element.getAsJsonObject();

            /* AMOUNT */
            if (fluidObject.has(AMOUNT)) {
                amount = fluidObject.get(AMOUNT).getAsInt();
            } else if (fluidObject.has(COUNT)) {
                amount = fluidObject.get(COUNT).getAsInt();
            }

            /* FLUID */
            if (fluidObject.has(FLUID)) {
                fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidObject.get(FLUID).getAsString()));
            }
            if (fluid == null) {
                return FluidStack.EMPTY;
            }
            stack = new FluidStack(fluid, amount);

            /* NBT */
            if (fluidObject.has(NBT)) {
                JsonElement nbtElement = fluidObject.get(NBT);
                CompoundNBT nbt;
                try {
                    if (nbtElement.isJsonObject()) {
                        nbt = JsonToNBT.getTagFromJson(GSON.toJson(nbtElement));
                    } else {
                        nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(nbtElement, NBT));
                    }
                    stack.setTag(nbt);
                } catch (Exception e) {
                    return FluidStack.EMPTY;
                }
            }

            //            /* NBT */
            //            if (fluidObject.has(NBT)) {
            //                try {
            //                    stack.setTag(JsonToNBT.getTagFromJson(fluidObject.get(NBT).getAsString()));
            //                } catch (Exception e) {
            //                    return FluidStack.EMPTY;
            //                }
            //            }
        }
        return stack;
    }

    private static float parseItemChance(JsonElement element) {

        JsonObject json = element.getAsJsonObject();

        if (json.has(CHANCE)) {
            float chance = json.get(CHANCE).getAsFloat();
            if (chance > 0.0F && json.has(LOCKED) && json.get(LOCKED).getAsBoolean()) {
                chance *= BASE_CHANCE_LOCKED;
            }
            return chance;
        }
        return BASE_CHANCE_LOCKED;
    }

    public static Block parseBlock(JsonElement element) {

        if (element == null || element.isJsonNull()) {
            return Blocks.AIR;
        }
        Block block;

        block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(element.getAsString()));
        return block == null ? Blocks.AIR : block;
    }
    // endregion

    // region STRING CONSTANTS
    public static final String ADJACENT = "adjacent";
    public static final String AMOUNT = "amount";
    public static final String AMPLIFIER = "amplifier";
    public static final String BASE = "base";
    public static final String BELOW = "below";
    public static final String CHANCE = "chance";
    public static final String COMMENT = "//";
    public static final String CONSTANT = "constant";
    public static final String COUNT = "count";
    public static final String CYCLES = "cycles";
    public static final String DEPENDENCY = "dependency";
    public static final String DURATION_MOD = "duration_mod";
    public static final String ENABLE = "enable";
    public static final String ENERGY = "energy";
    public static final String ENERGY_MOD = "energy_mod";
    public static final String EXPERIENCE = "experience";
    public static final String ENTRY = "entry";
    public static final String FLUID = "fluid";
    public static final String FLUID_TAG = "fluid";
    public static final String INGREDIENT = "ingredient";
    public static final String INGREDIENTS = "ingredients";
    public static final String INPUT = "input";
    public static final String INPUTS = "inputs";
    public static final String ITEM = "item";
    public static final String LOCKED = "locked";
    public static final String LOOT_TABLE = "loot_table";
    public static final String MIN_CHANCE = "min_chance";
    public static final String MOD_LOADED = "mod";
    public static final String NBT = "nbt";
    public static final String OUTPUT = "output";
    public static final String OUTPUT_MOD = "output_mod";
    public static final String OUTPUTS = "outputs";
    public static final String ORE = "ore";
    public static final String PRIMARY_MOD = "primary_mod";
    public static final String REMOVE = "remove";
    public static final String RESULT = "result";
    public static final String RESULTS = "results";
    public static final String SECONDARY_MOD = "secondary_mod";
    public static final String TAG = "tag";
    public static final String TICKS = "ticks";
    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final String USE_CHANCE = "use_chance";
    public static final String VALUE = "value";
    public static final String WILDCARD = "wildcard";
    public static final String XP = "xp";

    public static final String TRUNK = "trunk";
    public static final String LEAF = "leaf";
    public static final String LEAVES = "leaves";

    public static final String LAVA = "lava";
    public static final String WATER = "water";

    public static final String LAVA_MOD = "lava_mod";
    public static final String WATER_MOD = "water_mod";
    // endregion
}
