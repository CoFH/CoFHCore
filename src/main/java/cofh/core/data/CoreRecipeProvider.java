package cofh.core.data;

import cofh.core.init.CoreFlags;
import cofh.lib.data.RecipeProviderCoFH;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class CoreRecipeProvider extends RecipeProviderCoFH {

    public CoreRecipeProvider(DataGenerator generatorIn) {

        super(generatorIn, ID_COFH_CORE);
        manager = CoreFlags.manager();
    }

    @Override
    public String getName() {

        return "CoFH Core: Recipes";
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

    }

}
