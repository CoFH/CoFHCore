package cofh.core.util.crafting;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Copy of ShapedRecipe, but no dedicated serializer. Intended for recipes which wrap a Shaped Recipe and modify the output.
 */
public class ShapedRecipeInternal implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    public static final int MAX_WIDTH = 3;
    public static final int MAX_HEIGHT = 3;

    public final int width;
    public final int height;
    public final NonNullList<Ingredient> recipeItems;
    public final ItemStack result;
    public final ResourceLocation id;
    public final String group;

    public ShapedRecipeInternal(ResourceLocation p_i48162_1_, String p_i48162_2_, int p_i48162_3_, int p_i48162_4_, NonNullList<Ingredient> p_i48162_5_, ItemStack p_i48162_6_) {

        this.id = p_i48162_1_;
        this.group = p_i48162_2_;
        this.width = p_i48162_3_;
        this.height = p_i48162_4_;
        this.recipeItems = p_i48162_5_;
        this.result = p_i48162_6_;
    }

    public ResourceLocation getId() {

        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {

        return RecipeSerializer.SHAPED_RECIPE;
    }

    public String getGroup() {

        return this.group;
    }

    public ItemStack getResultItem() {

        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {

        return this.recipeItems;
    }

    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {

        return p_194133_1_ >= this.width && p_194133_2_ >= this.height;
    }

    public boolean matches(CraftingContainer p_77569_1_, Level p_77569_2_) {

        for (int i = 0; i <= p_77569_1_.getWidth() - this.width; ++i) {
            for (int j = 0; j <= p_77569_1_.getHeight() - this.height; ++j) {
                if (this.matches(p_77569_1_, i, j, true)) {
                    return true;
                }

                if (this.matches(p_77569_1_, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matches(CraftingContainer p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {

        for (int i = 0; i < p_77573_1_.getWidth(); ++i) {
            for (int j = 0; j < p_77573_1_.getHeight(); ++j) {
                int k = i - p_77573_2_;
                int l = j - p_77573_3_;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (p_77573_4_) {
                        ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(p_77573_1_.getItem(i + j * p_77573_1_.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(CraftingContainer p_77572_1_) {

        return this.getResultItem().copy();
    }

    public int getWidth() {

        return this.width;
    }

    public int getHeight() {

        return this.height;
    }

    @Override
    public int getRecipeWidth() {

        return getWidth();
    }

    @Override
    public int getRecipeHeight() {

        return getHeight();
    }

    public static NonNullList<Ingredient> dissolvePattern(String[] p_192402_0_, Map<String, Ingredient> p_192402_1_, int p_192402_2_, int p_192402_3_) {

        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_192402_2_ * p_192402_3_, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(p_192402_1_.keySet());
        set.remove(" ");

        for (int i = 0; i < p_192402_0_.length; ++i) {
            for (int j = 0; j < p_192402_0_[i].length(); ++j) {
                String s = p_192402_0_[i].substring(j, j + 1);
                Ingredient ingredient = p_192402_1_.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + p_192402_2_ * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    public static String[] shrink(String... p_194134_0_) {

        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < p_194134_0_.length; ++i1) {
            String s = p_194134_0_[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (p_194134_0_.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[p_194134_0_.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    public static int firstNonSpace(String p_194135_0_) {

        int i;
        for (i = 0; i < p_194135_0_.length() && p_194135_0_.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    public static int lastNonSpace(String p_194136_0_) {

        int i;
        for (i = p_194136_0_.length() - 1; i >= 0 && p_194136_0_.charAt(i) == ' '; --i) {
        }

        return i;
    }

    public static String[] patternFromJson(JsonArray p_192407_0_) {

        String[] astring = new String[p_192407_0_.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(p_192407_0_.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    public static Map<String, Ingredient> keyFromJson(JsonObject p_192408_0_) {

        Map<String, Ingredient> map = Maps.newHashMap();

        for (Entry<String, JsonElement> entry : p_192408_0_.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack itemFromJson(JsonObject p_199798_0_) {

        String s = GsonHelper.getAsString(p_199798_0_, "item");
        Item item = Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (p_199798_0_.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = GsonHelper.getAsInt(p_199798_0_, "count", 1);
            return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(p_199798_0_, true);
        }
    }

}
