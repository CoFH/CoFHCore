package cofh.core.util.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.Map;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.lib.util.references.CoreIDs.ID_CRAFTING_POTION;

public class ShapedPotionNBTRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    private final ShapedRecipeInternal wrappedRecipe;

    public ShapedPotionNBTRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result) {

        wrappedRecipe = new ShapedRecipeInternal(id, group, width, height, recipeItems, result);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {

        // boolean flag
        boolean potionItem = false;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == Items.POTION) {
                if (!PotionUtils.getMobEffects(stack).isEmpty()) {
                    potionItem = true;
                    break;
                }
            }
        }
        return potionItem && wrappedRecipe.matches(inv, worldIn);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {

        ItemStack result = wrappedRecipe.getResultItem().copy();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == Items.POTION && stack.getTag() != null) {
                result.setTag(stack.getTag().copy());
                break;
            }
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {

        return wrappedRecipe.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem() {

        return wrappedRecipe.getResultItem();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        return wrappedRecipe.getIngredients();
    }

    @Override
    public ResourceLocation getId() {

        return wrappedRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return RECIPE_SERIALIZERS.get(ID_CRAFTING_POTION);
    }

    // @Override
    public int getRecipeWidth() {

        return wrappedRecipe.getWidth();
    }

    // @Override
    public int getRecipeHeight() {

        return wrappedRecipe.getHeight();
    }

    // region SERIALIZER
    public static class Serializer implements RecipeSerializer<ShapedPotionNBTRecipe> {

        public ShapedPotionNBTRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            String s = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> map = ShapedRecipeInternal.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = ShapedRecipeInternal.shrink(ShapedRecipeInternal.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = ShapedRecipeInternal.dissolvePattern(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new ShapedPotionNBTRecipe(recipeId, s, i, j, nonnulllist, itemstack);
        }

        @Override
        public ShapedPotionNBTRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readUtf(32767);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.fromNetwork(buffer));
            }
            ItemStack itemstack = buffer.readItem();
            return new ShapedPotionNBTRecipe(recipeId, s, i, j, nonnulllist, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedPotionNBTRecipe recipe) {

            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.getResultItem());
        }

    }
    // endregion
}
