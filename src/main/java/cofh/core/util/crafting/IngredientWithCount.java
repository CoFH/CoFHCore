package cofh.core.util.crafting;

import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nullable;

public class IngredientWithCount extends AbstractIngredient {

    private Ingredient wrappedIngredient;
    private int count;

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
    public void toNetwork(FriendlyByteBuf buf) {

        wrappedIngredient.toNetwork(buf);
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

        return wrappedIngredient.getSerializer();
    }

    @Override
    public JsonElement toJson() {

        return wrappedIngredient.toJson();
    }

}