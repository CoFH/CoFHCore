package cofh.core.compat.jei;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;

import java.util.List;

public class PotionNBTRecipeManagerPlugin implements IRecipeManagerPlugin {

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {

        return List.of();
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {

        return List.of();
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {

        return List.of();
    }

}
