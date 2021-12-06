package cofh.core.util.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Map;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.lib.util.references.CoreIDs.ID_CRAFTING_POTION;

public class ShapedPotionNBTRecipe implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {

    private final ShapedRecipeInternal wrappedRecipe;

    public ShapedPotionNBTRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result) {

        wrappedRecipe = new ShapedRecipeInternal(id, group, width, height, recipeItems, result);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {

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
    public ItemStack assemble(CraftingInventory inv) {

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
    public IRecipeSerializer<?> getSerializer() {

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
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedPotionNBTRecipe> {

        public ShapedPotionNBTRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            String s = JSONUtils.getAsString(json, "group", "");
            Map<String, Ingredient> map = ShapedRecipeInternal.keyFromJson(JSONUtils.getAsJsonObject(json, "key"));
            String[] astring = ShapedRecipeInternal.shrink(ShapedRecipeInternal.patternFromJson(JSONUtils.getAsJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = ShapedRecipeInternal.dissolvePattern(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new ShapedPotionNBTRecipe(recipeId, s, i, j, nonnulllist, itemstack);
        }

        public ShapedPotionNBTRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {

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

        public void toNetwork(PacketBuffer buffer, ShapedPotionNBTRecipe recipe) {

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
