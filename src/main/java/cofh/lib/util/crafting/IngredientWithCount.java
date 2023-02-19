package cofh.lib.util.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;

public class IngredientWithCount extends AbstractIngredient {

    private final Ingredient wrappedIngredient;
    private final int count;

    public IngredientWithCount(Ingredient ingredient, int count) {

        this.wrappedIngredient = ingredient;
        this.count = count;
    }

    @Override
    public ItemStack[] getItems() {

        this.dissolve();
        return wrappedIngredient.itemStacks;
    }

    @Override
    public void dissolve() {

        wrappedIngredient.dissolve();
        if (wrappedIngredient.itemStacks == null) {
            return;
        }
        for (ItemStack stack : wrappedIngredient.itemStacks) {
            stack.setCount(count);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {

        return wrappedIngredient.test(stack);
    }

    @Override
    public IntList getStackingIds() {

        return wrappedIngredient.getStackingIds();
    }

    @Override
    public boolean isEmpty() {

        return wrappedIngredient.isEmpty();
    }

    @Override
    public boolean isSimple() {

        return wrappedIngredient.isSimple();
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {

        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {

        return wrappedIngredient.toJson();
    }

    public static class Serializer implements IIngredientSerializer<IngredientWithCount> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public IngredientWithCount parse(FriendlyByteBuf buffer) {

            return new IngredientWithCount(Ingredient.fromNetwork(buffer), buffer.readVarInt());
        }

        @Override
        public IngredientWithCount parse(JsonObject json) {

            throw new JsonSyntaxException("IngredientWithCount should not be parsed from JSON using the serializer, if you are a modder, use RecipeJsonUtils instead!");
        }

        @Override
        public void write(FriendlyByteBuf buffer, IngredientWithCount ingredient) {

            ingredient.wrappedIngredient.toNetwork(buffer);
            buffer.writeVarInt(ingredient.count);
        }

    }
}
