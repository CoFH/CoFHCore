package cofh.core.compat.jei;

import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PotionNBTRecipeManagerPlugin implements IRecipeManagerPlugin {

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {

        return List.of();
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {

        return List.of();
    }

    @Override
    public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {

        return List.of(VanillaRecipeCategoryUid.CRAFTING);
    }

}
