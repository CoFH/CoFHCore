package cofh.core.init.data.providers;

import cofh.core.util.CoreFlags;
import cofh.lib.init.data.RecipeProviderCoFH;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class CoreRecipeProvider extends RecipeProviderCoFH {

    public CoreRecipeProvider(PackOutput output) {

        super(output, ID_COFH_CORE);
        manager = CoreFlags.manager();
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

    }

}
