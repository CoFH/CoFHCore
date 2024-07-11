package cofh.core.util.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;

import static cofh.core.init.CoreRecipeSerializers.SHAPED_POTION_RECIPE_SERIALIZER;

public class ShapedPotionNBTRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    private final ShapedRecipe wrappedRecipe;

    public ShapedPotionNBTRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern pattern, ItemStack pResult) {

        wrappedRecipe = new ShapedRecipe(pGroup, pCategory, pattern, pResult);
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {

        ItemStack result = wrappedRecipe.getResultItem(registryAccess).copy();

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
    public ItemStack getResultItem(RegistryAccess registryAccess) {

        return wrappedRecipe.getResultItem(registryAccess);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        return wrappedRecipe.getIngredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return SHAPED_POTION_RECIPE_SERIALIZER.get();
    }

    @Override
    public int getRecipeWidth() {

        return wrappedRecipe.getWidth();
    }

    @Override
    public int getRecipeHeight() {

        return wrappedRecipe.getHeight();
    }

    @Override
    public CraftingBookCategory category() {

        return wrappedRecipe.category();
    }

    // region SERIALIZER
    public static class Serializer implements RecipeSerializer<ShapedPotionNBTRecipe> {

        public static final Codec<ShapedPotionNBTRecipe> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.wrappedRecipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.wrappedRecipe.category),
                                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.wrappedRecipe.pattern),
                                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.wrappedRecipe.result)
                        )
                        .apply(codec, ShapedPotionNBTRecipe::new)
        );

        @Override
        public Codec<ShapedPotionNBTRecipe> codec() {

            return CODEC;
        }

        public ShapedPotionNBTRecipe fromNetwork(FriendlyByteBuf buf) {

            String s = buf.readUtf();
            CraftingBookCategory craftingbookcategory = buf.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.fromNetwork(buf);
            ItemStack itemstack = buf.readItem();
            return new ShapedPotionNBTRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack);
        }

        public void toNetwork(FriendlyByteBuf buf, ShapedPotionNBTRecipe recipe) {

            buf.writeUtf(recipe.wrappedRecipe.group);
            buf.writeEnum(recipe.wrappedRecipe.category);
            recipe.wrappedRecipe.pattern.toNetwork(buf);
            buf.writeItem(recipe.wrappedRecipe.result);
        }

    }

    //    public static class Serializer implements RecipeSerializer<ShapedPotionNBTRecipe> {
    //
    //        public ShapedPotionNBTRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    //
    //            String s = GsonHelper.getAsString(json, "group", "");
    //            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String) null), CraftingBookCategory.MISC);
    //            Map<String, Ingredient> map = ShapedRecipeInternal.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
    //            String[] astring = ShapedRecipeInternal.shrink(ShapedRecipeInternal.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
    //            int i = astring[0].length();
    //            int j = astring.length;
    //            NonNullList<Ingredient> nonnulllist = ShapedRecipeInternal.dissolvePattern(astring, map, i, j);
    //            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
    //            return new ShapedPotionNBTRecipe(recipeId, s, craftingbookcategory, i, j, nonnulllist, itemstack);
    //        }
    //
    //        @Override
    //        public ShapedPotionNBTRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    //
    //            int i = buffer.readVarInt();
    //            int j = buffer.readVarInt();
    //            String s = buffer.readUtf(32767);
    //            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
    //            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
    //
    //            for (int k = 0; k < nonnulllist.size(); ++k) {
    //                nonnulllist.set(k, Ingredient.fromNetwork(buffer));
    //            }
    //            ItemStack itemstack = buffer.readItem();
    //            return new ShapedPotionNBTRecipe(recipeId, s, craftingbookcategory, i, j, nonnulllist, itemstack);
    //        }
    //
    //        @Override
    //        public void toNetwork(FriendlyByteBuf buffer, ShapedPotionNBTRecipe recipe) {
    //
    //            buffer.writeVarInt(recipe.getRecipeWidth());
    //            buffer.writeVarInt(recipe.getRecipeHeight());
    //            buffer.writeUtf(recipe.getGroup());
    //
    //            for (Ingredient ingredient : recipe.getIngredients()) {
    //                ingredient.toNetwork(buffer);
    //            }
    //            buffer.writeItem(recipe.wrappedRecipe.result);
    //        }
    //
    //    }
    // endregion
}
