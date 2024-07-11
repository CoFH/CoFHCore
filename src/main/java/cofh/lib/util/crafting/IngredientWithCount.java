package cofh.lib.util.crafting;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

public class IngredientWithCount extends Ingredient {

    private final Ingredient ingredient;
    private final int count;

    public IngredientWithCount(Ingredient ingredient, int count) {

        super(Stream.empty());

        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public ItemStack[] getItems() {

        if (ingredient.itemStacks == null) {
            ingredient.getItems();
            for (ItemStack stack : ingredient.itemStacks) {
                stack.setCount(count);
            }
        }
        return ingredient.getItems();
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {

        return stack != null && ingredient.test(stack) && stack.getCount() >= count;
    }

    @Override
    public IntList getStackingIds() {

        return ingredient.getStackingIds();
    }

    @Override
    public boolean isEmpty() {

        return ingredient.isEmpty();
    }

    @Override
    public boolean isSimple() {

        return ingredient.isSimple();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof IngredientWithCount other)) return false;
        return count == other.count && ingredient.equals(other.ingredient);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {

        return count + "x " + ingredient;
    }

}
